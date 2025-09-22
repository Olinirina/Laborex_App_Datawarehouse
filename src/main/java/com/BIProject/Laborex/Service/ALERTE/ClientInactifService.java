package com.BIProject.Laborex.Service.ALERTE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.ALERTE.AlerteDTO;
import com.BIProject.Laborex.Entity.DTO.ALERTE.NiveauSeverite;
import com.BIProject.Laborex.Entity.DTO.ALERTE.TypeAlerte;

@Service
public class ClientInactifService {

    @Autowired
    private SeuilCalculService seuilCalculService;

    /**
     * Détecte les clients qui n'ont pas acheté depuis longtemps
     */
    public List<AlerteDTO> detecterClientsInactifs(String search, String sortBy, String order) {
        List<AlerteDTO> alerte = new ArrayList<>();
        try {
            String sql = """
                SELECT
                     c.CodeCli,
                     c.NomCli,
                     COALESCE(EXTRACT(MONTH FROM AGE(
                         (SELECT MAX(CAST(DateValue AS DATE)) FROM DATE_PERSO),
                         MAX(CAST(d.DateValue AS DATE))
                     )), 12) AS mois_inactif,
                     COUNT(v.CodeVente) AS nb_ventes_historique
                 FROM CLIENT c
                 LEFT JOIN VENTE v ON c.CodeCli = v.CodeClient
                 LEFT JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
                 GROUP BY c.CodeCli, c.NomCli
                 HAVING mois_inactif >= 3
            """;

            List<AlerteDTO> temp = new ArrayList<>();

            try (Connection conn = DuckDBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String codeClient = rs.getString("CodeCli");
                    String nomClient = rs.getString("NomCli");
                    int moisInactif = rs.getInt("mois_inactif");
                    int nbVentesHistorique = rs.getInt("nb_ventes_historique");

                    // Ne pas alerter pour les clients qui n'ont jamais acheté
                    if (nbVentesHistorique == 0) continue;

                    NiveauSeverite severite = seuilCalculService.determinerSeveriteInactivite(moisInactif);

                    String description = String.format(
                        "Pas d'achat depuis %d mois. %d vente(s) dans l'historique",
                        moisInactif, nbVentesHistorique
                    );

                    AlerteDTO dto = new AlerteDTO();
                    dto.setCodeReference(codeClient);
                    dto.setDescription(description);
                    dto.setNomReference(nomClient);
                    dto.setSeverite(severite);
                    dto.setValeur(moisInactif);
                    dto.setType(TypeAlerte.CLIENT_INACTIF);

                    temp.add(dto);
                }
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

            System.out.println("Détection des clients inactifs terminée. Taille = " + alerte.size());

        } catch (Exception e) {
            System.err.println("Erreur lors de la détection des clients inactifs: " + e.getMessage());
            e.printStackTrace();
        }

        return alerte;
    }
}

