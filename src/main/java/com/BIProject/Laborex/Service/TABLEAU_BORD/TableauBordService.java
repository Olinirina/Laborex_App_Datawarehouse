package com.BIProject.Laborex.Service.TABLEAU_BORD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.ALERTE.AlerteDTO;
import com.BIProject.Laborex.Entity.DTO.ALERTE.NiveauSeverite;
import com.BIProject.Laborex.Entity.DTO.ALERTE.TopAlertesDTO;
import com.BIProject.Laborex.Entity.DTO.ALERTE.TypeAlerte;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT.Top5ClientCa;
import com.BIProject.Laborex.Entity.DTO.TABLEAU_BORD.GraphiqueTableauBord;
import com.BIProject.Laborex.Entity.DTO.TABLEAU_BORD.TableauBordDTO;
import com.BIProject.Laborex.Service.ALERTE.AnomalieVenteService;
import com.BIProject.Laborex.Service.ALERTE.ClientInactifService;
import com.BIProject.Laborex.Service.ALERTE.StockCritiqueService;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableauBordService {

    // services d'alerte - adapte les noms si nécessaires
    @Autowired private ClientInactifService clientInactifService;
    @Autowired private StockCritiqueService stockAlerteService;           // à adapter si nom diffère
    @Autowired private AnomalieVenteService anomalieVenteService;       // à adapter si nom diffère

    // ratio coût présumé (70% -> marge ≈ 30%)
    private static final double COST_RATIO = 0.7;

    private static final String[] MONTH_NAMES = {
        "", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };

    public TableauBordDTO buildTableauBord() {
        TableauBordDTO dto = new TableauBordDTO();

        try (Connection conn = DuckDBConnection.getConnection()) {

            // 1) CA global
            double caGlobal = queryDouble(conn, "SELECT COALESCE(SUM(Montant_Vente),0) FROM Vente");
            dto.setCaglobal(round2(caGlobal));

            // 2) Articles vendus (distinct count)
            double articlesVendus = queryDouble(conn, "SELECT COUNT(DISTINCT CodeArticle) FROM Vente");
            dto.setArticlesVendus(round2(articlesVendus));

            // 3) Clients actifs -> clients ayant acheté dans les 12 derniers mois (par rapport à la date max du DIM/DATE)
            int clientsActifs = 0;
            Date maxDateSql = queryDate(conn, "SELECT MAX(CAST(DateValue AS DATE)) FROM DATE_PERSO");
            if (maxDateSql != null) {
                LocalDate maxDate = maxDateSql.toLocalDate();
                LocalDate threshold = maxDate.minusMonths(12);
                String sqlClients = """
                		SELECT COUNT(DISTINCT v.CodeClient) AS cnt
						FROM Vente v
						JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
						WHERE CAST(d.DateValue AS DATE) >= (
						    SELECT MAX(CAST(DateValue AS DATE)) FROM DATE_PERSO
						) - INTERVAL 12 MONTH

                		""";
                try (PreparedStatement ps = conn.prepareStatement(sqlClients)) {                   
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) clientsActifs = rs.getInt("cnt");
                    }
                }
            } else {
                // fallback : tous les clients distincts dans les ventes
                clientsActifs = (int) queryDouble(conn, "SELECT COUNT(DISTINCT CodeClient) FROM Vente");
            }
            dto.setClientActifs(clientsActifs);

            // 4) Marge moyenne (en pourcentage)
            double margeMoyenne = 0.0;
            if (caGlobal > 0) {
                double totalCost = caGlobal * COST_RATIO;
                margeMoyenne = ((caGlobal - totalCost) / caGlobal) * 100.0;
            }
            dto.setMargeMoyenne(round2(margeMoyenne));

         // 5) Graphique :
            int lastYear = queryInt(conn, "SELECT COALESCE(MAX(Annee), 0) FROM DATE_PERSO");
            Map<Integer, Double> ventesByMonth = new HashMap<>();
            for (int m = 1; m <= 12; m++) ventesByMonth.put(m, 0.0);

            if (lastYear > 0) {
                String sqlMonth = """
                       SELECT 
					    EXTRACT(MONTH FROM CAST(d.DateValue AS DATE)) AS mois,
					    COUNT(v.CodeVente) AS nombreVente
					FROM Vente v
					JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
					WHERE d.Annee = ?
					GROUP BY EXTRACT(MONTH FROM CAST(d.DateValue AS DATE))
					ORDER BY mois

                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlMonth)) {
                    ps.setInt(1, lastYear); // filtrer sur l'année max trouvée
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int mois = rs.getInt("mois");
                            double ventes = rs.getDouble("nombreVente");
                            ventesByMonth.put(mois, ventesByMonth.getOrDefault(mois, 0.0) + ventes);
                        }
                    }
                }
            }

            List<GraphiqueTableauBord> graphiques = ventesByMonth.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> new GraphiqueTableauBord(round2(e.getValue()), monthName(e.getKey())))
                    .collect(Collectors.toList());

            dto.setGraphiques(graphiques);


            // 6) Top 5 clients par CA (avec pourcentage par rapport au CA global)
            List<Top5ClientCa> top5 = new ArrayList<>();
            String sqlTop5 = "SELECT c.NomCli, COALESCE(SUM(v.Montant_Vente),0) AS ca " +
                             "FROM Vente v JOIN CLIENT c ON v.CodeClient = c.CodeCli " +
                             "GROUP BY c.CodeCli, c.NomCli ORDER BY ca DESC LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(sqlTop5);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nom = rs.getString("NomCli");
                    double ca = rs.getDouble("ca");
                    double pct = (caGlobal > 0) ? (ca / caGlobal) * 100.0 : 0.0;
                    Top5ClientCa t = new Top5ClientCa();
                    t.setNomClient(nom);
                    t.setPourcentage(round2(pct));
                    top5.add(t);
                }
            }
            dto.setTopClients(top5);

            // 7) Top alertes : pour chaque type, récupérer alertes et garder max 3 de niveau ELEVE
            List<TopAlertesDTO> topAlertes = new ArrayList<>();

            // a) CLIENT_INACTIF
            try {
                List<AlerteDTO> allClientAlerts = clientInactifService.detecterClientsInactifs(null, null, null);
                List<AlerteDTO> topClient = selectTopBySeverity(allClientAlerts, NiveauSeverite.ELEVE, 3);
                for (AlerteDTO a : topClient) {
                    TopAlertesDTO t = new TopAlertesDTO();
                    t.setType(TypeAlerte.CLIENT_INACTIF);
                    t.setDescription(buildAlertLabel(a));
                    topAlertes.add(t);
                }
            } catch (Exception ex) { /* log si nécessaire */ }

            // b) STOCK_CRITIQUE
            try {
                List<AlerteDTO> allStock = stockAlerteService.detecterStocksCritiques(null, null, null);
                List<AlerteDTO> topStock = selectTopBySeverity(allStock, NiveauSeverite.ELEVE, 3);
                for (AlerteDTO a : topStock) {
                    TopAlertesDTO t = new TopAlertesDTO();
                    t.setType(TypeAlerte.STOCK_CRITIQUE);
                    t.setDescription(buildAlertLabel(a));
                    topAlertes.add(t);
                }
            } catch (Exception ex) { /* log si nécessaire */ }

            // c) ANOMALIE_VENTE
            try {
                List<AlerteDTO> allAnom = anomalieVenteService.detecterAnomaliesVente(null, null, null);
                List<AlerteDTO> topAnom = selectTopBySeverity(allAnom, NiveauSeverite.ELEVE, 3);
                for (AlerteDTO a : topAnom) {
                    TopAlertesDTO t = new TopAlertesDTO();
                    t.setType(TypeAlerte.ANOMALIE_VENTE);
                    t.setDescription(buildAlertLabel(a));
                    topAlertes.add(t);
                }
            } catch (Exception ex) { /* log si nécessaire */ }

            dto.setAlertes(topAlertes);

        } catch (SQLException ex) {
            ex.printStackTrace();
            // selon ton design : tu peux lever une RuntimeException ou renvoyer dto partiel
        }

        return dto;
    }

    // ---------- Helpers ----------

    private String monthName(int m) {
        if (m >= 1 && m <= 12) return MONTH_NAMES[m];
        return String.valueOf(m);
    }

    private String buildAlertLabel(AlerteDTO a) {
        // Formatage simple : "NomRef - description (valeur)"
        String nom = a.getNomReference() != null ? a.getNomReference() : a.getCodeReference();
        String desc = a.getDescription() != null ? a.getDescription() : "";
        String valeur = (a.getValeur() != null) ? (" (" + a.getValeur() + ")") : "";
        return nom + " - " + desc + valeur;
    }

    private List<AlerteDTO> selectTopBySeverity(List<AlerteDTO> list, NiveauSeverite severity, int limit) {
        if (list == null) return Collections.emptyList();
        return list.stream()
                .filter(a -> a.getSeverite() == severity)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double queryDouble(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    private int queryInt(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private java.sql.Date queryDate(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDate(1);
        }
        return null;
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

