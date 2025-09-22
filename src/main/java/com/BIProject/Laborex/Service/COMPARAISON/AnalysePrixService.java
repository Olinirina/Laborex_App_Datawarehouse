package com.BIProject.Laborex.Service.COMPARAISON;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.COMPARAISON.AnalysePrixDTO;
import com.BIProject.Laborex.Entity.DTO.COMPARAISON.ComparaisonDTO;
import com.BIProject.Laborex.Entity.DTO.COMPARAISON.SimulationDTO;
import com.BIProject.Laborex.Entity.DTO.COMPARAISON.StatutCompar;



@Service
public class AnalysePrixService {
	// Constantes pour les seuils d'alerte (configurables)
    private static final double SEUIL_MARGE_BASSE = 10.0; // 10%
    private static final double SEUIL_PRIX_ELEVE = 15.0; // 15%
    /**
     *ANALYSE COMPLETE
     * - Différences concurrentielles
     * - Statut de positionnement
     * - Classement concurrentiel
     * - Alertes de marge et prix
     * - Min/Max des prix concurrents
     */
    public AnalysePrixDTO calculerAnalyseComplete(String search, String sortBy, String order) {
        List<ComparaisonDTO> resultats = new ArrayList<>();
        AnalysePrixDTO analyse = new AnalysePrixDTO();

        String sql = """
            WITH prix_stats AS (
                SELECT 
                    a.CodeArticle,
                    a.LibArticle,
                    a.PrixVente,
                    MIN(comp.PrixConcurrent) as min_prix_concurrent,
                    MAX(comp.PrixConcurrent) as max_prix_concurrent,
                    AVG(comp.PrixConcurrent) as avg_prix_concurrent,
                    COUNT(comp.PrixConcurrent) as nb_concurrents
                FROM ARTICLE a
                LEFT JOIN COMPARAISON comp ON a.CodeArticle = comp.CodeArticle
                WHERE a.PrixVente IS NOT NULL AND a.PrixVente > 0
                GROUP BY a.CodeArticle, a.LibArticle, a.PrixVente
            ),
            classements AS (
                SELECT 
                    ps.CodeArticle,
                    ps.PrixVente,
                    -- Calcul du classement : nombre de concurrents moins chers + 1
                    (SELECT COUNT(*) + 1 
                     FROM COMPARAISON c2 
                     WHERE c2.CodeArticle = ps.CodeArticle 
                     AND c2.PrixConcurrent < ps.PrixVente) as classement
                FROM prix_stats ps
            )
            SELECT 
                ps.CodeArticle,
                ps.LibArticle,
                ps.PrixVente,
                c.CodeConcurrent,
                c.NomConcurrent,
                comp.PrixConcurrent,
                (comp.PrixConcurrent - ps.PrixVente) as difference_absolue,
                CASE 
                    WHEN ps.PrixVente > 0 THEN 
                        ROUND(((comp.PrixConcurrent - ps.PrixVente) / ps.PrixVente) * 100, 2)
                    ELSE 0 
                END as difference_pourcentage,
                ps.min_prix_concurrent,
                ps.max_prix_concurrent,
                cls.classement,
                -- Calcul de la marge (supposons 30% de coût, à adapter selon vos données)
                ROUND(((ps.PrixVente - (ps.PrixVente * 0.7)) / ps.PrixVente) * 100, 2) as marge_actuelle
            FROM prix_stats ps
            LEFT JOIN COMPARAISON comp ON ps.CodeArticle = comp.CodeArticle
            LEFT JOIN CONCURRENT c ON comp.CodeConcurrent = c.CodeConcurrent
            LEFT JOIN classements cls ON ps.CodeArticle = cls.CodeArticle
            WHERE comp.PrixConcurrent IS NOT NULL
            """;

        List<ComparaisonDTO> temp = new ArrayList<>();
        try (Connection conn = DuckDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Double prixVente = rs.getDouble("PrixVente");
                Double prixConcurrent = rs.getDouble("PrixConcurrent");
                Double differencePourcentage = rs.getDouble("difference_pourcentage");
                Double margeActuelle = rs.getDouble("marge_actuelle");
                Double minPrix = rs.getDouble("min_prix_concurrent");
                Double maxPrix = rs.getDouble("max_prix_concurrent");
                Integer classement = rs.getInt("classement");

                // Calcul du statut de positionnement
                StatutCompar statutPositionnement = calculerStatutPositionnement(prixVente, prixConcurrent);
                
                // Calcul des alertes
                Boolean alerteMargeBasse = margeActuelle < SEUIL_MARGE_BASSE;
                Boolean alertePrixEleve = Math.abs(differencePourcentage) > SEUIL_PRIX_ELEVE && differencePourcentage > 0;

                ComparaisonDTO dto = new ComparaisonDTO(
                    rs.getString("CodeArticle"),
                    rs.getString("LibArticle"),
                    prixVente,
                    null,
                    null,
                    null,
                    null,
                    differencePourcentage,
                    statutPositionnement,
                    classement,
                    alerteMargeBasse,
                    alertePrixEleve,
                    minPrix,
                    maxPrix,
                    margeActuelle
                );
                temp.add(dto);
            }
         // === Appliquer filtre et tri sur temp ===
            Stream<ComparaisonDTO> stream = temp.stream();

            // Filtre recherche
            if (search != null && !search.isEmpty()) {
                String lowerSearch = search.toLowerCase();
                stream = stream.filter(a ->
                        a.getCodeArticle().toLowerCase().contains(lowerSearch) ||
                        a.getLibelleArticle().toLowerCase().contains(lowerSearch) 
                );
            }

            // Tri dynamique
            Comparator<ComparaisonDTO> comparator;
            if ("code".equalsIgnoreCase(sortBy)) {
                comparator = Comparator.comparing(ComparaisonDTO::getCodeArticle, String.CASE_INSENSITIVE_ORDER);
            } else if ("nom".equalsIgnoreCase(sortBy)) {
                comparator = Comparator.comparing(ComparaisonDTO::getLibelleArticle, String.CASE_INSENSITIVE_ORDER);
            } else if ("prix".equalsIgnoreCase(sortBy)) {
                comparator = Comparator.comparing(ComparaisonDTO::getPrixVenteArticle);
            } else {
                // valeur numérique par défaut (assure-toi que getValeur() existe dans ton DTO)
                comparator = Comparator.comparingDouble(ComparaisonDTO::getMargeActuelle);
            }

            if ("desc".equalsIgnoreCase(order)) {
                comparator = comparator.reversed();
            }

            resultats = stream.sorted(comparator).collect(Collectors.toList());

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'analyse complète : " + e.getMessage());
            throw new RuntimeException("Erreur d'analyse des prix", e);
        }
     // === Remplissage de AnalysePrixDTO ===
        analyse.setCompar(resultats);
     // Calculs globaux
        double margeMoyenne = resultats.stream()
                .mapToDouble(ComparaisonDTO::getMargeActuelle)
                .average()
                .orElse(0.0);

        double pourcentagePrix = resultats.stream()
                .mapToDouble(c -> c.getDifferencePourcentage() != null ? c.getDifferencePourcentage() : 0.0)
                .average()
                .orElse(0.0);

        int classementMoyen = (int) resultats.stream()
                .mapToInt(c -> c.getClassementConcurrentiel() != null ? c.getClassementConcurrentiel() : 0)
                .average()
                .orElse(0.0);

        analyse.setMargeBasse(margeMoyenne);
        analyse.setPourcentagePrix(pourcentagePrix);
        analyse.setClassementMoyen(classementMoyen);
        return analyse;

    }
    
    /**
     * ANALYSE POUR UN ARTICLE 
     */
    public List<ComparaisonDTO> calculerAnalysePourArticle(String codeArticle,String search, String sortBy, String order) {
        List<ComparaisonDTO> resultats = new ArrayList<>();

        String sql = """
            WITH prix_stats AS (
                SELECT 
                    a.CodeArticle,
                    a.LibArticle,
                    a.PrixVente,
                    MIN(comp.PrixConcurrent) as min_prix_concurrent,
                    MAX(comp.PrixConcurrent) as max_prix_concurrent,
                    AVG(comp.PrixConcurrent) as avg_prix_concurrent
                FROM ARTICLE a
                LEFT JOIN COMPARAISON comp ON a.CodeArticle = comp.CodeArticle
                WHERE a.CodeArticle = ? AND a.PrixVente IS NOT NULL AND a.PrixVente > 0
                GROUP BY a.CodeArticle, a.LibArticle, a.PrixVente
            ),
            classement AS (
                SELECT 
                    (SELECT COUNT(*) + 1 
                     FROM COMPARAISON c2 
                     WHERE c2.CodeArticle = ? 
                     AND c2.PrixConcurrent < ps.PrixVente) as rang
                FROM prix_stats ps
            )
            SELECT 
                ps.CodeArticle,
                ps.LibArticle,
                ps.PrixVente,
                c.CodeConcurrent,
                c.NomConcurrent,
                comp.PrixConcurrent,
                (comp.PrixConcurrent - ps.PrixVente) as difference_absolue,
                CASE 
                    WHEN ps.PrixVente > 0 THEN 
                        ROUND(((comp.PrixConcurrent - ps.PrixVente) / ps.PrixVente) * 100, 2)
                    ELSE 0 
                END as difference_pourcentage,
                ps.min_prix_concurrent,
                ps.max_prix_concurrent,
                cls.rang as classement,
                ROUND(((ps.PrixVente - (ps.PrixVente * 0.7)) / ps.PrixVente) * 100, 2) as marge_actuelle
            FROM prix_stats ps
            LEFT JOIN COMPARAISON comp ON ps.CodeArticle = comp.CodeArticle
            LEFT JOIN CONCURRENT c ON comp.CodeConcurrent = c.CodeConcurrent
            CROSS JOIN classement cls
            WHERE comp.PrixConcurrent IS NOT NULL
            """;

        List<ComparaisonDTO> temp = new ArrayList<>();
        try (Connection conn = DuckDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codeArticle);
            pstmt.setString(2, codeArticle);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Double prixVente = rs.getDouble("PrixVente");
                    Double prixConcurrent = rs.getDouble("PrixConcurrent");
                    Double differencePourcentage = rs.getDouble("difference_pourcentage");
                    Double margeActuelle = rs.getDouble("marge_actuelle");
                    
                    StatutCompar statutPositionnement = calculerStatutPositionnement(prixVente, prixConcurrent);
                    Boolean alerteMargeBasse = margeActuelle < SEUIL_MARGE_BASSE;
                    Boolean alertePrixEleve = Math.abs(differencePourcentage) > SEUIL_PRIX_ELEVE && differencePourcentage > 0;

                    ComparaisonDTO dto = new ComparaisonDTO(
                        rs.getString("CodeArticle"),
                        rs.getString("LibArticle"),
                        prixVente,
                        rs.getString("CodeConcurrent"),
                        rs.getString("NomConcurrent"),
                        prixConcurrent,
                        rs.getDouble("difference_absolue"),
                        differencePourcentage,
                        statutPositionnement,
                        rs.getInt("classement"),
                        alerteMargeBasse,
                        alertePrixEleve,
                        rs.getDouble("min_prix_concurrent"),
                        rs.getDouble("max_prix_concurrent"),
                        margeActuelle
                    );
                    temp.add(dto);
                }
             // === Appliquer filtre et tri sur temp ===
                Stream<ComparaisonDTO> stream = temp.stream();

                // Filtre recherche
                if (search != null && !search.isEmpty()) {
                    String lowerSearch = search.toLowerCase();
                    stream = stream.filter(a ->
                            a.getCodeArticle().toLowerCase().contains(lowerSearch) ||
                            a.getLibelleArticle().toLowerCase().contains(lowerSearch) ||
                            a.getNomConcurrent().toLowerCase().contains(lowerSearch) ||
                            a.getStatutPositionnement().name().toLowerCase().contains(lowerSearch)
                    );
                }

                // Tri dynamique
                Comparator<ComparaisonDTO> comparator;
                if ("code".equalsIgnoreCase(sortBy)) {
                    comparator = Comparator.comparing(ComparaisonDTO::getCodeArticle, String.CASE_INSENSITIVE_ORDER);
                } else if ("nom".equalsIgnoreCase(sortBy)) {
                    comparator = Comparator.comparing(ComparaisonDTO::getLibelleArticle, String.CASE_INSENSITIVE_ORDER);
                } else if ("prix".equalsIgnoreCase(sortBy)) {
                    comparator = Comparator.comparing(ComparaisonDTO::getPrixVenteArticle);
                }else if ("concurrent".equalsIgnoreCase(sortBy)) {
                    comparator = Comparator.comparing(ComparaisonDTO::getNomConcurrent, String.CASE_INSENSITIVE_ORDER);
                } else {
                    // valeur numérique par défaut (assure-toi que getValeur() existe dans ton DTO)
                    comparator = Comparator.comparingDouble(ComparaisonDTO::getMargeActuelle);
                }

                if ("desc".equalsIgnoreCase(order)) {
                    comparator = comparator.reversed();
                }

                resultats = stream.sorted(comparator).collect(Collectors.toList());

            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'analyse pour l'article " + codeArticle + " : " + e.getMessage());
            throw new RuntimeException("Erreur d'analyse des prix", e);
        }

        return resultats;
    }
    
    /**
     * Simule un nouveau prix et calcule l'impact sur toutes les métriques
     * Cette méthode permet de :
     * - Tester différents scénarios de prix
     * - Calculer l'impact sur la marge
     * - Prévoir le nouveau classement concurrentiel
     * - Analyser l'impact sur le positionnement
     */
    public List<SimulationDTO> simulerTousLesArticles() {
        List<SimulationDTO> simulations = new ArrayList<>();

        String sql = """
           WITH donnees_actuelles AS (
		    SELECT 
		        a.CodeArticle,
		        a.LibArticle,
		        a.PrixVente as prix_actuel,
		        MIN(comp.PrixConcurrent) as min_prix,
		        MAX(comp.PrixConcurrent) as max_prix,
		        AVG(comp.PrixConcurrent) as avg_prix,
		        (SELECT COUNT(*) + 1 
		         FROM COMPARAISON c2 
		         WHERE c2.CodeArticle = a.CodeArticle 
		         AND c2.PrixConcurrent < a.PrixVente) as classement_actuel
		    FROM ARTICLE a
		    LEFT JOIN COMPARAISON comp ON a.CodeArticle = comp.CodeArticle
		    WHERE a.PrixVente IS NOT NULL 
		      AND a.PrixVente > 0
		    GROUP BY a.CodeArticle, a.LibArticle, a.PrixVente
		)
		SELECT 
		    CodeArticle,
		    LibArticle,
		    prix_actuel,
		    min_prix,
		    max_prix,
		    avg_prix,
		    classement_actuel
		FROM donnees_actuelles;

            """;

        try (Connection conn = DuckDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                SimulationDTO simulation = new SimulationDTO();

                String codeArticle = rs.getString("CodeArticle");
                String libelle = rs.getString("LibArticle");
                double prixActuel = rs.getDouble("prix_actuel");
                double minPrix = rs.getDouble("min_prix");
                double maxPrix = rs.getDouble("max_prix");
                double avgPrix = rs.getDouble("avg_prix");
                int classementActuel = rs.getInt("classement_actuel");

                // Exemple de règle : nouveau prix = moyenne entre min et max
                double nouveauPrix = (minPrix + maxPrix) / 2;

                // Calculs marges
                double margeActuelle = ((prixActuel - (prixActuel * 0.7)) / prixActuel) * 100;
                double nouvelleMarge = ((nouveauPrix - (nouveauPrix * 0.7)) / nouveauPrix) * 100;

                // Écarts concurrentiels
                double ecartActuel = ((prixActuel - avgPrix) / avgPrix) * 100;
                double nouvelEcart = ((nouveauPrix - avgPrix) / avgPrix) * 100;

                // Nouveau classement
                int nouveauClassement = calculerNouveauClassement(conn, codeArticle, nouveauPrix);

                // Impact positionnement
                String impactPositionnement = calculerImpactPositionnement(classementActuel, nouveauClassement);
                if (rs.wasNull() || Double.isNaN(nouveauPrix)) {
                    continue; // ignorer cet article
                }

                // Remplir DTO
                simulation.setCodeArticle(codeArticle);
                simulation.setLibelleArticle(libelle);
                simulation.setPrixVenteActuel(prixActuel);
                simulation.setNouveauPrixVente(nouveauPrix);
                simulation.setMargeActuelle(margeActuelle);
                simulation.setNouvelleMarge(nouvelleMarge);
                simulation.setEcartConcurrentielActuel(ecartActuel);
                simulation.setNouvelEcartConcurrentiel(nouvelEcart);
                simulation.setClassementActuel(classementActuel);
                simulation.setNouveauClassement(nouveauClassement);
                simulation.setImpactPositionnement(impactPositionnement);
                simulation.setAmeliorationClassement(nouveauClassement < classementActuel);

                simulations.add(simulation);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la simulation : " + e.getMessage());
            throw new RuntimeException("Erreur de simulation", e);
        }

        return simulations;
    }

    
    /**
     * METHODES UTILITAIRES
     */
    private StatutCompar calculerStatutPositionnement(Double prixVente, Double prixConcurrent) {
        if (prixVente == null || prixConcurrent == null) {
            return null;
        }
        
        double difference = Math.abs(prixVente - prixConcurrent);
        double pourcentageDifference = (difference / prixConcurrent) * 100;
        
        if (pourcentageDifference < 1.0) {
            return StatutCompar.EGAL;
        } else if (prixVente < prixConcurrent) {
            return StatutCompar.MOINS_CHER;
        } else {
            return StatutCompar.PLUS_CHER;
        }
    }
    
    /**
     * Calcule l'impact d'un changement de prix sur le classement
     * Analyse l'évolution du positionnement concurrentiel
     */
    private String calculerImpactPositionnement(Integer classementActuel, Integer nouveauClassement) {
        if (classementActuel == null || nouveauClassement == null) {
            return "Indéterminé";
        }
        
        int difference = nouveauClassement - classementActuel;
        
        if (difference == 0) {
            return "Aucun changement";
        } else if (difference < 0) {
            return "Amélioration de " + Math.abs(difference) + " place(s)";
        } else {
            return "Dégradation de " + difference + " place(s)";
        }
    }
    private int calculerNouveauClassement(Connection conn, String codeArticle, double nouveauPrix) throws SQLException {
        String sqlClassement = """
            SELECT COUNT(*) + 1 as classement
            FROM COMPARAISON
            WHERE CodeArticle = ?
            AND PrixConcurrent < ?
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sqlClassement)) {
            pstmt.setString(1, codeArticle);
            pstmt.setDouble(2, nouveauPrix);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("classement");
                }
            }
        }
        return -1; // si rien trouvé
    }

    

}
