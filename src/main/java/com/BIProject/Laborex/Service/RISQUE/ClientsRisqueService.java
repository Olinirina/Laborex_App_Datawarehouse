package com.BIProject.Laborex.Service.RISQUE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.RISQUE.CamembertRisque;
import com.BIProject.Laborex.Entity.DTO.RISQUE.ClientRisqueDTO;
import com.BIProject.Laborex.Entity.DTO.RISQUE.RisqueDTO;
import com.BIProject.Laborex.Entity.DTO.RISQUE.SegmentClientRisque;
import com.BIProject.Laborex.Entity.DTO.RISQUE.TopClientsRisques;

import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;



@Service
public class ClientsRisqueService {

    // ========= 1) Requête d’agrégats mensuels par client =========
    private List<MonthlyClientAgg> loadMonthlyClientAgg() {
        String sql = """
            SELECT v.CodeClient AS code_cli,
                   c.NomCli   AS lib_cli,
                   d.Annee    AS annee,
                   d.Mois     AS mois,
                   SUM(v.Quantite_Vendu)  AS total_qte,
                   SUM(v.Montant_Vente)    AS total_ca,
                   COUNT(*)                AS nb_lignes
            FROM VENTE v
            JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
            JOIN CLIENT c     ON v.CodeClient  = c.CodeCli
            GROUP BY v.CodeClient, c.NomCli, d.Annee, d.Mois
            ORDER BY v.CodeClient, d.Annee, d.Mois
        """;

        List<MonthlyClientAgg> list = new ArrayList<>();
        try (Connection conn = DuckDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MonthlyClientAgg m = new MonthlyClientAgg();
                m.codeCli = rs.getString("code_cli");
                m.libCli  = rs.getString("lib_cli");
                m.annee   = rs.getInt("annee");
                m.mois    = rs.getInt("mois");
                m.totalQte= rs.getDouble("total_qte");
                m.totalCA = rs.getDouble("total_ca");
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    // ========= 2) Calcul du DTO principal =========
    public RisqueDTO scoreClientsWeka(int windowMonths, int horizonMonths,
            String search, String sortBy, String order) throws Exception {
			List<ClientRisqueDTO> raw = scoreClientsWekaRaw(windowMonths, horizonMonths);
			
			if (raw.isEmpty()) return new RisqueDTO();
			
			// === 1) Calcul taux global ===
			double tauxGlobal = raw.stream()
			.mapToDouble(ClientRisqueDTO::getProbaIn)
			.average()
			.orElse(0.0);
			
			// === 2) Top 5 clients ===
			List<TopClientsRisques> top5 = raw.stream()
			.sorted(Comparator.comparingDouble(ClientRisqueDTO::getProbaIn).reversed())
			.limit(5)
			.map(c -> {
			TopClientsRisques t = new TopClientsRisques();
			t.setNomClient(c.getNomClient());
			return t;
			})
			.collect(Collectors.toList());
			
			// === 3) Camembert ===
			Map<SegmentClientRisque, Long> countBySegment = raw.stream()
			.collect(Collectors.groupingBy(
			c -> segmentOf(c.getProbaIn()),
			Collectors.counting()
			));
			
			long total = raw.size();
			List<CamembertRisque> camembert = countBySegment.entrySet().stream()
			.map(e -> {
			CamembertRisque c = new CamembertRisque();
			c.setSegment(e.getKey());
			c.setPourcentage(Math.round((e.getValue() * 100.0 / total) * 100.0) / 100.0);
			return c;
			})
			.collect(Collectors.toList());
			
			// === 4) Clients détaillés ===
			List<ClientRisqueDTO> clients = raw.stream()
			.map(c -> {
			ClientRisqueDTO dto = new ClientRisqueDTO();
			dto.setCodeClient(c.getCodeClient());
			dto.setNomClient(c.getNomClient());
			dto.setProbaIn(c.getProbaIn());
			dto.setFreqAchat(c.getFreqAchat());
			dto.setMoisIn(c.getMoisIn());
			dto.setCaCumul(c.getCaCumul());
			dto.setSegment(segmentOf(c.getProbaIn()));
			return dto;
			})
			.collect(Collectors.toList());
			
			// === 4a) Filtre recherche sur le tableau clients uniquement ===
			if (search != null && !search.isEmpty()) {
			String lowerSearch = search.toLowerCase();
			clients = clients.stream()
			.filter(c -> c.getNomClient().toLowerCase().contains(lowerSearch)
			  || c.getCodeClient().toLowerCase().contains(lowerSearch))
			.collect(Collectors.toList());
			}
			
			// === 4b) Tri sur le tableau clients uniquement ===
			if (sortBy != null && !sortBy.isEmpty()) {
			Comparator<ClientRisqueDTO> comparator = null;
			if ("code".equalsIgnoreCase(sortBy)) {
			comparator = Comparator.comparing(ClientRisqueDTO::getCodeClient, String.CASE_INSENSITIVE_ORDER);
			} else if ("nom".equalsIgnoreCase(sortBy)) {
			comparator = Comparator.comparing(ClientRisqueDTO::getNomClient, String.CASE_INSENSITIVE_ORDER);
			} else if ("taux".equalsIgnoreCase(sortBy)) {
			comparator = Comparator.comparingDouble(ClientRisqueDTO::getProbaIn);
			}
			
			if (comparator != null) {
			if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();
			clients = clients.stream().sorted(comparator).collect(Collectors.toList());
			}
			}
			
			// === 5) Assemblage DTO ===
			RisqueDTO dto = new RisqueDTO();
			dto.setTauxGlobal(tauxGlobal);
			dto.setTop5(top5);
			dto.setCamembert(camembert);
			dto.setClients(clients);
			
			return dto;
}


    

    // 🚨 à implémenter ou à importer depuis RiskPredictionService
    private List<ClientRisqueDTO> scoreClientsWekaRaw(int windowMonths, int horizonMonths) throws Exception {
    	 // a) Charger les agrégats
        List<MonthlyClientAgg> raw = loadMonthlyClientAgg();
        if (raw.isEmpty()) return Collections.emptyList();

        // b) Grouper par client et fabriquer des timelines complètes (mois manquants = 0)
        Map<String, List<MonthlyClientAgg>> byClient = raw.stream()
                .collect(Collectors.groupingBy(m -> m.codeCli, LinkedHashMap::new, Collectors.toList()));

        // c) Construit le dataset d’apprentissage
        Instances train = buildEmptyDataset();
        List<Timeline> timelines = new ArrayList<>();

        for (Map.Entry<String, List<MonthlyClientAgg>> e : byClient.entrySet()) {
            String codeCli = e.getKey();
            String libCli  = e.getValue().get(0).libCli;

            Timeline tl = buildCompleteTimeline(e.getValue(), codeCli, libCli);
            timelines.add(tl);

            // Pour chaque mois t (suffisamment de passé et de futur), construire un exemple (features + label)
            for (int t = windowMonths - 1; t <= tl.maxIndex - horizonMonths; t++) {
                FeatureVector fv = makeFeatures(tl, t, windowMonths);
                int labelInactive = isInactiveNextHorizon(tl, t, horizonMonths) ? 1 : 0; // 1=inactive, 0=active
                Instance inst = makeLabeledInstance(train, fv, labelInactive);
                train.add(inst);
            }
        }

        if (train.isEmpty()) {
            // Pas de quoi entraîner (pas assez d’historique)
            return Collections.emptyList();
        }

        // d) Entraîner un classifieur probabiliste
        Classifier model = trainLogistic(train);

        // e) Scorer chaque client sur son "dernier mois" (risque d’inactivité sur le futur horizon)
        List<ClientRisqueDTO> out = new ArrayList<>();
        for (Timeline tl : timelines) {
            int t = tl.maxIndex; // dernier mois disponible
            if (t < windowMonths - 1) continue; // pas assez d’historique pour ce client
            FeatureVector fv = makeFeatures(tl, t, windowMonths);

            Instance unlabeled = makeUnlabeledInstance(train, fv);
            double[] dist = model.distributionForInstance(unlabeled);
            // Ordre des classes = ["active","inactive"] (voir buildEmptyDataset)
            double probaInactive = dist[1];

            ClientRisqueDTO dto = new ClientRisqueDTO();
            dto.setCodeClient(tl.codeCli);
            dto.setNomClient(tl.libCli);
            dto.setProbaIn(round4(probaInactive));
            dto.setSegment(segmentOf(probaInactive));
            dto.setFreqAchat(fv.freq6);
            dto.setCaCumul(round0(fv.ca6));
            dto.setMoisIn(tl.monthOf(t));

            out.add(dto);
        }

        // Tri: proba décroissante
        out.sort(Comparator.comparingDouble(ClientRisqueDTO::getProbaIn).reversed());
        return out;
    }
 // ========= STRUCTURE DATASET WEKA =========
    private Instances buildEmptyDataset() {
        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("freq6"));       // 0..6
        attrs.add(new Attribute("gaps6"));       // 0..6
        attrs.add(new Attribute("ca6"));         // somme CA 6 mois
        attrs.add(new Attribute("qte6"));        // somme Qté 6 mois
        attrs.add(new Attribute("last_ca"));     // CA du mois t
        attrs.add(new Attribute("last_qte"));    // Qté du mois t

        ArrayList<String> cls = new ArrayList<>();
        cls.add("active");
        cls.add("inactive");
        attrs.add(new Attribute("target", cls)); // nominal binaire

        Instances data = new Instances("ClientInactivity", attrs, 1024);
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    private Classifier trainLogistic(Instances train) throws Exception {
        Logistic logit = new Logistic();
        logit.setMaxIts(200);
        logit.buildClassifier(train);
        return logit;
    }

    private Instance makeLabeledInstance(Instances ref, FeatureVector fv, int labelInactive01) {
        double[] v = new double[ref.numAttributes()];
        v[0] = fv.freq6;
        v[1] = fv.gaps6;
        v[2] = fv.ca6;
        v[3] = fv.qte6;
        v[4] = fv.lastCA;
        v[5] = fv.lastQte;
        // class (0=active, 1=inactive)
        v[6] = labelInactive01 == 1
                ? ref.classAttribute().indexOfValue("inactive")
                : ref.classAttribute().indexOfValue("active");
        return new DenseInstance(1.0, v);
    }

    private Instance makeUnlabeledInstance(Instances ref, FeatureVector fv) {
        double[] v = new double[ref.numAttributes()];
        v[0] = fv.freq6;
        v[1] = fv.gaps6;
        v[2] = fv.ca6;
        v[3] = fv.qte6;
        v[4] = fv.lastCA;
        v[5] = fv.lastQte;
        v[6] = Utils.missingValue(); // classe inconnue
        Instance inst = new DenseInstance(1.0, v);
        inst.setDataset(ref);
        return inst;
    }

    // ========= Features & Timeline =========

    private static class MonthlyClientAgg {
        String codeCli;
        String libCli;
        int annee;
        int mois;
        double totalQte;
        double totalCA;
    }

    private static class Timeline {
        String codeCli;
        String libCli;
        // periodIndex = annee*12 + (mois-1)
        int minIndex;
        int maxIndex;
        // Map<periodIndex, Pair(qte, ca)>
        Map<Integer, double[]> values = new HashMap<>();

        int toIndex(int year, int month) { return year * 12 + (month - 1); }
        int yearOf(int index) { return index / 12; }
        int monthOf(int index) { return (index % 12) + 1; }
    }

    private Timeline buildCompleteTimeline(List<MonthlyClientAgg> rows, String codeCli, String libCli) {
        Timeline tl = new Timeline();
        tl.codeCli = codeCli;
        tl.libCli = libCli;

        int minIdx = Integer.MAX_VALUE;
        int maxIdx = Integer.MIN_VALUE;

        for (MonthlyClientAgg r : rows) {
            int idx = r.annee * 12 + (r.mois - 1);
            tl.values.put(idx, new double[]{ r.totalQte, r.totalCA });
            if (idx < minIdx) minIdx = idx;
            if (idx > maxIdx) maxIdx = idx;
        }
        tl.minIndex = minIdx;
        tl.maxIndex = maxIdx;

        // Remplir les mois absents avec 0
        for (int idx = minIdx; idx <= maxIdx; idx++) {
            tl.values.computeIfAbsent(idx, k -> new double[]{0d, 0d});
        }
        return tl;
    }

    private static class FeatureVector {
        int freq6;
        int gaps6;
        double ca6;
        double qte6;
        double lastCA;
        double lastQte;
    }

    private FeatureVector makeFeatures(Timeline tl, int t, int windowMonths) {
        FeatureVector fv = new FeatureVector();
        int start = t - (windowMonths - 1);

        int freq = 0;
        int gaps = 0;
        double caSum = 0d;
        double qteSum = 0d;

        for (int k = start; k <= t; k++) {
            double[] v = tl.values.getOrDefault(k, new double[]{0d, 0d});
            double q = v[0];
            double ca = v[1];
            if (q > 0.0 || ca > 0.0) freq++;
            else gaps++;
            caSum += ca;
            qteSum += q;
        }

        double[] last = tl.values.getOrDefault(t, new double[]{0d, 0d});

        fv.freq6 = freq;
        fv.gaps6 = gaps;
        fv.ca6 = caSum;
        fv.qte6 = qteSum;
        fv.lastCA = last[1];
        fv.lastQte = last[0];
        return fv;
    }

    private boolean isInactiveNextHorizon(Timeline tl, int t, int horizonMonths) {
        int start = t + 1;
        int end = t + horizonMonths;
        for (int k = start; k <= end; k++) {
            double[] v = tl.values.getOrDefault(k, new double[]{0d, 0d});
            if (v[0] > 0.0 || v[1] > 0.0) {
                return false; // achat observé -> actif
            }
        }
        return true; // aucun achat observé -> inactif
    }

    // ========= Utilitaires =========
    private static SegmentClientRisque segmentOf(double p) {
        if (p >= 0.70) return SegmentClientRisque.HIGH;
        if (p >= 0.40) return SegmentClientRisque.MEDIUM;
        return SegmentClientRisque.LOW;
    }

    private static double round0(double x) { return Math.rint(x); }      // arrondi 0 déc.
    private static double round4(double x) { return Math.round(x * 1e4) / 1e4; } // 4 déc.
}