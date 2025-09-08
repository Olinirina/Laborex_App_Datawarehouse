package com.BIProject.Laborex.Service.PREVISON;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC.AbcDto;
import com.BIProject.Laborex.Entity.DTO.PREVISION.DemandePrevisionDTO;
import com.BIProject.Laborex.Entity.DTO.PREVISION.GraphiqueDemandeDTO;
import com.BIProject.Laborex.Entity.DTO.PREVISION.PrevisionDTO;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.Utils;


@Service
public class PrevisionService {

    // 🔹 Charger les ventes historiques par article
    public Map<String, List<DemandePrevisionDTO>> getMonthlySalesByArticle() {
        String query = """
            SELECT v.CodeArticle, a.LibArticle, d.Mois, SUM(v.Quantite_Vendu) AS total_ventes
            FROM VENTE v
            JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
            JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle
            GROUP BY v.CodeArticle, a.LibArticle, d.Mois
            ORDER BY v.CodeArticle, d.Mois
        """;

        Map<String, List<DemandePrevisionDTO>> ventesMap = new HashMap<>();

        try (Connection conn = DuckDBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("CodeArticle");
                String lib = rs.getString("LibArticle");
                int mois = rs.getInt("Mois");
                double total = rs.getDouble("total_ventes");

                DemandePrevisionDTO dto = new DemandePrevisionDTO();
                dto.setCodeArticle(code);
                dto.setLibelle(lib);
                dto.setMois(mois);
                dto.setQuantitePrevue(total); // ici c’est historique mais sera remplacé par la prédiction

                ventesMap.computeIfAbsent(code, k -> new ArrayList<>()).add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventesMap;
    }

    // Construire dataset pour Weka
    private Instances buildDataset(List<DemandePrevisionDTO> ventes, String article) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Mois"));
        attributes.add(new Attribute("QuantiteVendu"));

        Instances dataset = new Instances("Ventes_" + article, attributes, ventes.size());
        dataset.setClassIndex(1);

        for (DemandePrevisionDTO v : ventes) {
            double[] values = {v.getMois(), v.getQuantitePrevue()}; // quantité réelle historique
            dataset.add(new DenseInstance(1.0, values));
        }
        return dataset;
    }

    private Classifier trainModel(Instances dataset) throws Exception {
        LinearRegression model = new LinearRegression();
        model.buildClassifier(dataset);
        return model;
    }

    private double predictForMonth(Classifier model, int mois, Instances dataset) throws Exception {
        double[] values = {mois, Utils.missingValue()};
        Instance newInstance = new DenseInstance(1.0, values);
        newInstance.setDataset(dataset);
        return model.classifyInstance(newInstance); // ⚡ ceci est la quantité prévue pour le mois
    }

 // Méthode principale pour 3 mois prévisionnels
    public PrevisionDTO predictNext3Months(String search, String sortBy, String order) {
        Map<String, List<DemandePrevisionDTO>> ventesParArticle = getMonthlySalesByArticle();
        List<DemandePrevisionDTO> tableau = new ArrayList<>(); // ✅ tous les enregistrements bruts
        Map<String, GraphiqueDemandeDTO> graphiqueMap = new HashMap<>();
        int[] moisPrevision = {1, 2, 3}; // Jan, Fev, Mar

        for (String code : ventesParArticle.keySet()) {
            List<DemandePrevisionDTO> ventes = ventesParArticle.get(code);
            String lib = ventes.get(0).getLibelle();

            try {
                Instances dataset = buildDataset(ventes, code);
                Classifier model = trainModel(dataset);

                GraphiqueDemandeDTO graphique = new GraphiqueDemandeDTO();
                graphique.setCodeArticle(code);
                graphique.setLibelle(lib);

                for (int mois : moisPrevision) {
                    double forecast = predictForMonth(model, mois, dataset);

                    DemandePrevisionDTO dto = new DemandePrevisionDTO();
                    dto.setCodeArticle(code);
                    dto.setLibelle(lib);
                    dto.setMois(mois);
                    dto.setQuantitePrevue(Math.round(forecast));

                    tableau.add(dto); // ✅ ajout brut

                    switch (mois) {
                        case 1 -> graphique.setQuantiteJan(Math.round(forecast));
                        case 2 -> graphique.setQuantiteFev(Math.round(forecast));
                        case 3 -> graphique.setQuantiteMar(Math.round(forecast));
                    }
                }

                graphiqueMap.put(code, graphique);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ✅ Création du flux pour filtrer et trier uniquement le tableau
        Stream<DemandePrevisionDTO> stream = tableau.stream();

        // 🔎 Filtre recherche
        if (search != null && !search.isEmpty()) {
            String lowerSearch = search.toLowerCase();
            stream = stream.filter(a ->
                    a.getCodeArticle().toLowerCase().contains(lowerSearch) ||
                    a.getLibelle().toLowerCase().contains(lowerSearch)
            );
        }

        // 🔽 Tri dynamique
        Comparator<DemandePrevisionDTO> comparator;
        if ("code".equalsIgnoreCase(sortBy))
            comparator = Comparator.comparing(DemandePrevisionDTO::getCodeArticle, String.CASE_INSENSITIVE_ORDER);
        else if ("nom".equalsIgnoreCase(sortBy))
            comparator = Comparator.comparing(DemandePrevisionDTO::getLibelle, String.CASE_INSENSITIVE_ORDER);
        else if ("quantite".equalsIgnoreCase(sortBy))
            comparator = Comparator.comparing(DemandePrevisionDTO::getQuantitePrevue);
        else
            comparator = Comparator.comparingInt(DemandePrevisionDTO::getMois);

        if ("desc".equalsIgnoreCase(order))
            comparator = comparator.reversed();

        List<DemandePrevisionDTO> demande = stream.sorted(comparator).collect(Collectors.toList());

        // 🔹 Top 5 pour graphique (reste inchangé)
        List<GraphiqueDemandeDTO> topGraphique = graphiqueMap.values().stream()
                .sorted(Comparator.comparingDouble(g -> -(g.getQuantiteJan() + g.getQuantiteFev() + g.getQuantiteMar())))
                .limit(5)
                .collect(Collectors.toList());

        // ✅ DTO final
        PrevisionDTO result = new PrevisionDTO();
        result.setTableau(demande);    // filtré + trié
        result.setGraphiques(topGraphique); // indépendant du filtre

        return result;
    }


}

