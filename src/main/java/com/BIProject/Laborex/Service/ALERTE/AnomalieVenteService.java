package com.BIProject.Laborex.Service.ALERTE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.ALERTE.AlerteDTO;
import com.BIProject.Laborex.Entity.DTO.ALERTE.NiveauSeverite;
import com.BIProject.Laborex.Entity.DTO.ALERTE.TypeAlerte;

@Service
public class AnomalieVenteService {
    
    /**
     * Détecte les anomalies dans les ventes (pics ou chutes inhabituelles)
     */
     public List<AlerteDTO> detecterAnomaliesVente(String search, String sortBy, String order) {
        System.out.println("Début de la détection des anomalies de vente");
        List<AlerteDTO> alerte = new ArrayList<>();
        
        try {
            String sql = """
                WITH ventes_recentes AS (
                     SELECT
                         v.CodeArticle,
                         a.LibArticle,
                         SUM(v.Quantite_Vendu) AS ventes_7_jours
                     FROM VENTE v
                     JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
                     JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle
                     WHERE
                         CAST(d.DateValue AS DATE) >= (SELECT MAX(CAST(DateValue AS DATE)) FROM DATE_PERSO) - INTERVAL '7 days'
                    GROUP BY
                         v.CodeArticle,
                         a.LibArticle
                 ), ventes_moyennes AS (
                     SELECT
                         v.CodeArticle,
                         AVG(v.Quantite_Vendu) * 7 AS moyenne_7_jours_mensuelle
                     FROM VENTE v
                     JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
                     WHERE
                         CAST(d.DateValue AS DATE) >= (SELECT MAX(CAST(DateValue AS DATE)) FROM DATE_PERSO) - INTERVAL '30 days'
                        AND CAST(d.DateValue AS DATE) < (SELECT MAX(CAST(DateValue AS DATE)) FROM DATE_PERSO) - INTERVAL '7 days'
                     GROUP BY
                         v.CodeArticle
                 )
                 SELECT
                     r.CodeArticle,
                     r.LibArticle,
                     r.ventes_7_jours,
                    COALESCE(m.moyenne_7_jours_mensuelle, 0) AS moyenne_attendue,
                     CASE
                         WHEN COALESCE(m.moyenne_7_jours_mensuelle, 0) = 0 THEN 0
                         ELSE ABS(r.ventes_7_jours - m.moyenne_7_jours_mensuelle) / m.moyenne_7_jours_mensuelle * 100
                    END AS pourcentage_ecart
                 FROM ventes_recentes r
                 LEFT JOIN ventes_moyennes m ON r.CodeArticle = m.CodeArticle
                 WHERE
                     CASE
                        WHEN COALESCE(m.moyenne_7_jours_mensuelle, 0) = 0 THEN 0
                        ELSE ABS(r.ventes_7_jours - COALESCE(m.moyenne_7_jours_mensuelle, 0)) / COALESCE(m.moyenne_7_jours_mensuelle, 1) * 100
                     END >= 50.0
            """;
            List<AlerteDTO> temp = new ArrayList<>();
            try (Connection conn = DuckDBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                try (ResultSet rs = pstmt.executeQuery()) {
                    int anomaliesDetectees = 0;
                    while (rs.next()) {
                        String codeArticle = rs.getString("CodeArticle");
                        String libelle = rs.getString("LibArticle");
                        Double ventesRecentes = rs.getDouble("ventes_7_jours");
                        Double moyenneAttendue = rs.getDouble("moyenne_attendue");
                        Double pourcentageEcart = rs.getDouble("pourcentage_ecart");
                        
                        // Déterminer si c'est un pic ou une chute
                        boolean estUnPic = ventesRecentes > moyenneAttendue;
                        String typeAnomalie = estUnPic ? "pic" : "chute";
                        
                        // Déterminer la sévérité basée sur le pourcentage d'écart
                        NiveauSeverite severite;
                        if (pourcentageEcart >= 200) severite = NiveauSeverite.CRITIQUE;
                        else if (pourcentageEcart >= 100) severite = NiveauSeverite.ELEVE;
                        else if (pourcentageEcart >= 75) severite = NiveauSeverite.MODERE;
                        else severite = NiveauSeverite.FAIBLE;
                        String description = String.format(
                            "Anomalie: %s de vente inhabituel. " +
                            "Ventes des 7 derniers jours: %.0f unités (moyenne attendue: %.0f unités). " +
                            "Écart: %.1f%%. %s",
                            typeAnomalie, ventesRecentes, moyenneAttendue, pourcentageEcart,
                            estUnPic ? "Vérifier la disponibilité du stock." : 
                                      "Analyser les causes de la baisse de demande."
                        );
                        AlerteDTO dto= new AlerteDTO();
                        dto.setCodeReference(codeArticle);
                        dto.setNomReference(libelle);
                        dto.setDescription(description);
                        dto.setSeverite(severite);
                        dto.setType(TypeAlerte.ANOMALIE_VENTE);
                        dto.setValeur((int) Math.round(ventesRecentes));
                        temp.add(dto);
                        anomaliesDetectees++;
                    }
                    System.out.println("Détection des anomalies de vente terminée. " + anomaliesDetectees + " anomalies détectées.");
                }
                // === Appliquer filtre et tri sur temp ===
                Stream<AlerteDTO> stream = temp.stream();

                // Filtre recherche
                if (search != null && !search.isEmpty()) {
                    String lowerSearch = search.toLowerCase();
                    stream = stream.filter(a ->
                            a.getCodeReference().toLowerCase().contains(lowerSearch) ||
                            a.getNomReference().toLowerCase().contains(lowerSearch) ||
                            a.getDescription().toLowerCase().contains(lowerSearch) ||
                            a.getType().name().toLowerCase().contains(lowerSearch)
                    );
                }

                // Tri dynamique
                Comparator<AlerteDTO> comparator;
                if ("code".equalsIgnoreCase(sortBy)) {
                    comparator = Comparator.comparing(AlerteDTO::getCodeReference, String.CASE_INSENSITIVE_ORDER);
                } else if ("nom".equalsIgnoreCase(sortBy)) {
                    comparator = Comparator.comparing(AlerteDTO::getNomReference, String.CASE_INSENSITIVE_ORDER);
                } else if ("niveau".equalsIgnoreCase(sortBy)) {
                    comparator = Comparator.comparing(AlerteDTO::getSeverite);
                } else {
                    // valeur numérique par défaut (assure-toi que getValeur() existe dans ton DTO)
                    comparator = Comparator.comparingDouble(AlerteDTO::getValeur);
                }

                if ("desc".equalsIgnoreCase(order)) {
                    comparator = comparator.reversed();
                }

                alerte = stream.sorted(comparator).collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la détection des anomalies de vente" + e);
        }
        return alerte;
    }
    
}
