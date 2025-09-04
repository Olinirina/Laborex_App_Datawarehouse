package com.BIProject.Laborex.Service.SEGMENTATION;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC.AbcDto;
import com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC.CamembertClassification;
import com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC.CategorieClassification;
import com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC.ClassificationABCDTO;
import com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM.CategorieRFMClient;
import com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM.ClientRfmDto;
import com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM.GraphiquesRFM;
import com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM.SegmentationRFMDTO;

@Service
public class SegmentationService {
	// ==================== CLASSIFICATION ABC /ARTICLES ====================
	public ClassificationABCDTO getAbcAnalyseParArticle(String search, String sortBy, String order) {
	    ClassificationABCDTO resultat = new ClassificationABCDTO();

	    List<AbcDto> temp= new ArrayList<>();
	    // 1. Requête SQL pour récupérer les articles et leur CA
	    String sql = """
	        SELECT
	            a.CodeArticle,
	            a.LibArticle,
	            SUM(v.Montant_Vente) AS ChiffreAffaires
	        FROM VENTE v
	        JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle
	        GROUP BY a.CodeArticle, a.LibArticle
	    """;

	    try (Connection conn = DuckDBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	    	
	        while (rs.next()) {
	            temp.add(new AbcDto(
	                rs.getString("CodeArticle"),
	                rs.getString("LibArticle"),
	                rs.getDouble("ChiffreAffaires"),
	                0, // Pourcentage à calculer après
	                null // Catégorie à calculer après
	            ));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return new ClassificationABCDTO();
	    }

	    if (temp.isEmpty()) {
	        return new ClassificationABCDTO();
	    }

	    double caTotal = temp.stream().mapToDouble(AbcDto::getChiffreAffaires).sum();
	 // 2. Trier par CA décroissant avant classification
	    temp.sort(Comparator.comparingDouble(AbcDto::getChiffreAffaires).reversed());

	 // 3. Calcul du CA cumulé et attribution des classes
	    double caCumule = 0;
	    for (AbcDto item : temp) {
	        double pct = (caTotal > 0) ? (item.getChiffreAffaires() / caTotal) * 100 : 0;
	        item.setPourcentageCa(pct);

	        caCumule += item.getChiffreAffaires();
	        double pourcentageCumule = (caCumule / caTotal) * 100;

	        if (pourcentageCumule <= 80) item.setCategorie(CategorieClassification.A);
	        else if (pourcentageCumule <= 95) item.setCategorie(CategorieClassification.B);
	        else item.setCategorie(CategorieClassification.C);
	    }

	 // Calcul du camembert sur la liste complète temp
	    List<CamembertClassification> camembert = new ArrayList<>();
	    camembert.add(new CamembertClassification("A",
	            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.A)
	                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));
	    camembert.add(new CamembertClassification("B",
	            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.B)
	                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));
	    camembert.add(new CamembertClassification("C",
	            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.C)
	                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));

	    // 5. Application du filtre + tri uniquement sur le tableau
	    //Creer un flux (pipeline) pour enchainer les filtres et tri sans modifier la liste d'origine
	    Stream<AbcDto> stream = temp.stream();

	 // Filtre de recherche
	    if (search != null && !search.isEmpty()) {
	        String lowerSearch = search.toLowerCase();
	        stream = stream.filter(a ->
	                a.getCodeElement().toLowerCase().contains(lowerSearch) ||
	                a.getNomElement().toLowerCase().contains(lowerSearch) ||
	                a.getCategorie().name().toLowerCase().contains(lowerSearch)
	        );
	    }

	    // Construction dynamique du comparateur
	    Comparator<AbcDto> comparator;
	    if ("code".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getCodeElement, String.CASE_INSENSITIVE_ORDER);
	    else if ("nom".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getNomElement, String.CASE_INSENSITIVE_ORDER);
	    else if ("categorie".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getCategorie);
	    else comparator = Comparator.comparingDouble(AbcDto::getChiffreAffaires);

	    if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();

	    // Materialiser le résultat filtré et trié
	    List<AbcDto> articles = stream.sorted(comparator).collect(Collectors.toList());


	    
	    // 6. Préparer le résultat final
	    resultat.setAbcDto(articles);
	    resultat.setCamembert(camembert);

	    return resultat;
	}
	
	// ==================== CLASSIFICATION ABC /CLIENTS ====================
		public ClassificationABCDTO getAbcAnalyseParClients(String search, String sortBy, String order) {
			ClassificationABCDTO resultat = new ClassificationABCDTO();

		    List<AbcDto> temp= new ArrayList<>();
		    // 1. Requête SQL pour récupérer les articles et leur CA
		    String sql = """
		        SELECT
			            c.CodeCli, c.NomCli,
			            SUM(v.Montant_Vente) AS ChiffreAffaires
			        FROM VENTE v
			        JOIN CLIENT c ON v.CodeClient = c.CodeCli
			        GROUP BY c.CodeCli,c.NomCli
		    """;

		    try (Connection conn = DuckDBConnection.getConnection();
		         Statement stmt = conn.createStatement();
		         ResultSet rs = stmt.executeQuery(sql)) {

		    	
		        while (rs.next()) {
		            temp.add(new AbcDto(
		                rs.getString("CodeCli"),
		                rs.getString("NomCli"),
		                rs.getDouble("ChiffreAffaires"),
		                0, // Pourcentage à calculer après
		                null // Catégorie à calculer après
		            ));
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        return new ClassificationABCDTO();
		    }

		    if (temp.isEmpty()) {
		        return new ClassificationABCDTO();
		    }

		    double caTotal = temp.stream().mapToDouble(AbcDto::getChiffreAffaires).sum();
			 // 2. Trier par CA décroissant avant classification
			    temp.sort(Comparator.comparingDouble(AbcDto::getChiffreAffaires).reversed());

			 // 3. Calcul du CA cumulé et attribution des classes
			    double caCumule = 0;
			    for (AbcDto item : temp) {
			        double pct = (caTotal > 0) ? (item.getChiffreAffaires() / caTotal) * 100 : 0;
			        item.setPourcentageCa(pct);

			        caCumule += item.getChiffreAffaires();
			        double pourcentageCumule = (caCumule / caTotal) * 100;

			        if (pourcentageCumule <= 80) item.setCategorie(CategorieClassification.A);
			        else if (pourcentageCumule <= 95) item.setCategorie(CategorieClassification.B);
			        else item.setCategorie(CategorieClassification.C);
			    }

		 // Calcul du camembert sur la liste complète temp
		    List<CamembertClassification> camembert = new ArrayList<>();
		    camembert.add(new CamembertClassification("A",
		            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.A)
		                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));
		    camembert.add(new CamembertClassification("B",
		            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.B)
		                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));
		    camembert.add(new CamembertClassification("C",
		            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.C)
		                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));

		    // 5. Application du filtre + tri uniquement sur le tableau
		    //Creer un flux (pipeline) pour enchainer les filtres et tri sans modifier la liste d'origine
		    Stream<AbcDto> stream = temp.stream();

		 // Filtre de recherche
		    if (search != null && !search.isEmpty()) {
		        String lowerSearch = search.toLowerCase();
		        stream = stream.filter(a ->
		                a.getCodeElement().toLowerCase().contains(lowerSearch) ||
		                a.getNomElement().toLowerCase().contains(lowerSearch) ||
		                a.getCategorie().name().toLowerCase().contains(lowerSearch)
		        );
		    }

		    // Construction dynamique du comparateur
		    Comparator<AbcDto> comparator;
		    if ("code".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getCodeElement, String.CASE_INSENSITIVE_ORDER);
		    else if ("nom".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getNomElement, String.CASE_INSENSITIVE_ORDER);
		    else if ("categorie".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getCategorie);
		    else comparator = Comparator.comparingDouble(AbcDto::getChiffreAffaires);

		    if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();

		    // Materialiser le résultat filtré et trié
		    List<AbcDto> clients = stream.sorted(comparator).collect(Collectors.toList());


		    
		    // 6. Préparer le résultat final
		    resultat.setAbcDto(clients);
		    resultat.setCamembert(camembert);

		    return resultat;
		}
		
		// ==================== CLASSIFICATION ABC /LABOS ====================
				public ClassificationABCDTO getAbcAnalyseParLabos(String search, String sortBy, String order) {
					ClassificationABCDTO resultat = new ClassificationABCDTO();

				    List<AbcDto> temp= new ArrayList<>();
				    // 1. Requête SQL pour récupérer les articles et leur CA
				    String sql = """
				       SELECT
					            l.CodeLabo,l.NomLabo,
					            SUM(v.Montant_Vente) AS ChiffreAffaires
					        FROM VENTE v
					        JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle
					        JOIN LABO l ON a.CodeLabo = l.CodeLabo
					        GROUP BY l.CodeLabo,l.NomLabo	       
				    """;

				    try (Connection conn = DuckDBConnection.getConnection();
				         Statement stmt = conn.createStatement();
				         ResultSet rs = stmt.executeQuery(sql)) {

				    	
				        while (rs.next()) {
				            temp.add(new AbcDto(
				                rs.getString("CodeLabo"),
				                rs.getString("NomLabo"),
				                rs.getDouble("ChiffreAffaires"),
				                0, // Pourcentage à calculer après
				                null // Catégorie à calculer après
				            ));
				        }

				    } catch (SQLException e) {
				        e.printStackTrace();
				        return new ClassificationABCDTO();
				    }

				    if (temp.isEmpty()) {
				        return new ClassificationABCDTO();
				    }

				    double caTotal = temp.stream().mapToDouble(AbcDto::getChiffreAffaires).sum();
					 // 2. Trier par CA décroissant avant classification
					    temp.sort(Comparator.comparingDouble(AbcDto::getChiffreAffaires).reversed());

					 // 3. Calcul du CA cumulé et attribution des classes
					    double caCumule = 0;
					    for (AbcDto item : temp) {
					        double pct = (caTotal > 0) ? (item.getChiffreAffaires() / caTotal) * 100 : 0;
					        item.setPourcentageCa(pct);

					        caCumule += item.getChiffreAffaires();
					        double pourcentageCumule = (caCumule / caTotal) * 100;

					        if (pourcentageCumule <= 80) item.setCategorie(CategorieClassification.A);
					        else if (pourcentageCumule <= 95) item.setCategorie(CategorieClassification.B);
					        else item.setCategorie(CategorieClassification.C);
					    }

				 // Calcul du camembert sur la liste complète temp
				    List<CamembertClassification> camembert = new ArrayList<>();
				    camembert.add(new CamembertClassification("A",
				            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.A)
				                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));
				    camembert.add(new CamembertClassification("B",
				            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.B)
				                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));
				    camembert.add(new CamembertClassification("C",
				            temp.stream().filter(a -> a.getCategorie() == CategorieClassification.C)
				                .mapToDouble(AbcDto::getChiffreAffaires).sum() / caTotal * 100));

				    // 5. Application du filtre + tri uniquement sur le tableau
				    //Creer un flux (pipeline) pour enchainer les filtres et tri sans modifier la liste d'origine
				    Stream<AbcDto> stream = temp.stream();

				 // Filtre de recherche
				    if (search != null && !search.isEmpty()) {
				        String lowerSearch = search.toLowerCase();
				        stream = stream.filter(a ->
				                a.getCodeElement().toLowerCase().contains(lowerSearch) ||
				                a.getNomElement().toLowerCase().contains(lowerSearch) ||
				                a.getCategorie().name().toLowerCase().contains(lowerSearch)
				        );
				    }

				    // Construction dynamique du comparateur
				    Comparator<AbcDto> comparator;
				    if ("code".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getCodeElement, String.CASE_INSENSITIVE_ORDER);
				    else if ("nom".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getNomElement, String.CASE_INSENSITIVE_ORDER);
				    else if ("categorie".equalsIgnoreCase(sortBy)) comparator = Comparator.comparing(AbcDto::getCategorie);
				    else comparator = Comparator.comparingDouble(AbcDto::getChiffreAffaires);

				    if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();

				    // Materialiser le résultat filtré et trié
				    List<AbcDto> labos = stream.sorted(comparator).collect(Collectors.toList());


				    
				    // 6. Préparer le résultat final
				    resultat.setAbcDto(labos);
				    resultat.setCamembert(camembert);

				    return resultat;
				}
				
				
				// ==================== SEGMENTATION RFM CLIENTS ====================
				public SegmentationRFMDTO getRFMSegmentation(String search, String sortBy, String order) {
					SegmentationRFMDTO resultat= new SegmentationRFMDTO();
					
					List<ClientRfmDto> tempClients= new ArrayList<>();
					
					//Recuperer les donnees RFM depuis la base
					String sql= """
								SELECT
						            c.CodeCli,
						            c.NomCli,
						            MAX(d.DateValue) AS DerniereCommande,
						            COUNT(DISTINCT v.CodeVente) AS FrequenceAchat,
						            SUM(v.Montant_Vente) AS MontantTotal
						        FROM Vente v
						        JOIN CLIENT c ON v.CodeClient = c.CodeCli
						        JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
						        GROUP BY c.CodeCli, c.NomCli
							""";
					try (Connection conn = DuckDBConnection.getConnection();
					         Statement stmt = conn.createStatement();
					         ResultSet rs = stmt.executeQuery(sql)) {

					        LocalDate aujourdHui = LocalDate.now();

					        while (rs.next()) {
					            java.sql.Date sqlDate = rs.getDate("DerniereCommande");
					            LocalDate derniereCommande = (sqlDate != null) ? sqlDate.toLocalDate() : null;

					            long recence = (derniereCommande != null) ? ChronoUnit.DAYS.between(derniereCommande, aujourdHui) : -1;
					            long frequence = rs.getLong("FrequenceAchat");
					            double montant = rs.getDouble("MontantTotal");

					            tempClients.add(new ClientRfmDto(
					                    rs.getString("CodeCli"),
					                    rs.getString("NomCli"),
					                    recence,
					                    frequence,
					                    montant,
					                    0, 0, 0,  // Scores RFM à calculer
					                    null       // Segment à attribuer plus tard
					            ));
					        }
					    } catch (SQLException e) {
					        e.printStackTrace();
					        return new SegmentationRFMDTO();
					    }
					if (tempClients.isEmpty()) return new SegmentationRFMDTO();

				    // 2. Calculer les scores RFM
				    calculerScoresRFM(tempClients);

				    // 3. Segmenter les clients et ajouter l'interprétation
				    segmenterClients(tempClients);

				   
				    
				    // 4. Construire la synthèse pour le graphique (nombre de clients par segment)
				    Map<CategorieRFMClient, Long> mapGraphique = tempClients.stream()
				            .collect(Collectors.groupingBy(ClientRfmDto::getSegment, Collectors.counting()));

				    List<GraphiquesRFM> graphiques = new ArrayList<>();
				    for (CategorieRFMClient cat : CategorieRFMClient.values()) {
				        int nombre = mapGraphique.getOrDefault(cat, 0L).intValue();
				        graphiques.add(new GraphiquesRFM(cat, nombre));
				    }

				    // 5. Filtrer et trier le tableau (sans affecter le graphique)
				    Stream<ClientRfmDto> stream = tempClients.stream();

				    if (search != null && !search.isEmpty()) {
				        String lowerSearch = search.toLowerCase();
				        stream = stream.filter(c ->
				                c.getCodeClient().toLowerCase().contains(lowerSearch) ||
				                c.getNomClient().toLowerCase().contains(lowerSearch) ||
				                (c.getSegment() != null && c.getSegment().name().toLowerCase().contains(lowerSearch))
				        );
				    }

				    Comparator<ClientRfmDto> comparator;
				    if ("code".equalsIgnoreCase(sortBy)) {
				        comparator = Comparator.comparing(ClientRfmDto::getCodeClient, String.CASE_INSENSITIVE_ORDER);
				    } else if ("nom".equalsIgnoreCase(sortBy)) {
				        comparator = Comparator.comparing(ClientRfmDto::getNomClient, String.CASE_INSENSITIVE_ORDER);
				    } else if ("segment".equalsIgnoreCase(sortBy)) {
				        comparator = Comparator.comparing(ClientRfmDto::getSegment);
				    } else {
				        comparator = Comparator.comparingLong(ClientRfmDto::getFrequence); // par défaut tri sur fréquence
				    }

				    if ("desc".equalsIgnoreCase(order)) {
				        comparator = comparator.reversed();
				    }

				    List<ClientRfmDto> clientsFiltres = stream.sorted(comparator).collect(Collectors.toList());

				    // 6. Remplir le DTO final
				    resultat.setClients(clientsFiltres);
				    resultat.setGraphiques(graphiques);

				    return resultat;
				}
				
				
				
				// ==================== Methodes utilitaires pour la SEGMENTATION RFM CLIENTS ====================
				private void calculerScoresRFM(List<ClientRfmDto> clients) {
				    int n = clients.size();

				    // Récence : utiliser une copie de la liste pour le tri
				    List<ClientRfmDto> recenceSorted = new ArrayList<>(clients);
				    recenceSorted.sort(Comparator.comparing(ClientRfmDto::getRecence));
				    for (int i = 0; i < n; i++) {
				        int score = 4 - (int) Math.floor((double) i / n * 4);
				        recenceSorted.get(i).setScoreR(score);
				    }

				    // Fréquence : copie pour le tri
				    List<ClientRfmDto> frequenceSorted = new ArrayList<>(clients);
				    frequenceSorted.sort(Comparator.comparing(ClientRfmDto::getFrequence).reversed());
				    for (int i = 0; i < n; i++) {
				        int score = (int) Math.floor((double) i / n * 4) + 1;
				        frequenceSorted.get(i).setScoreF(score);
				    }

				    // Montant : copie pour le tri
				    List<ClientRfmDto> montantSorted = new ArrayList<>(clients);
				    montantSorted.sort(Comparator.comparing(ClientRfmDto::getMontant).reversed());
				    for (int i = 0; i < n; i++) {
				        int score = (int) Math.floor((double) i / n * 4) + 1;
				        montantSorted.get(i).setScoreM(score);
				    }
				}

				
				private void segmenterClients(List<ClientRfmDto> clients) {
				    for (ClientRfmDto c : clients) {
				        int r = c.getScoreR();
				        int f = c.getScoreF();
				        int m = c.getScoreM();

				        if (r >= 4 && f >= 4 && m >= 4) c.setSegment(CategorieRFMClient.CHAMPION);
				        else if (r >= 4 && f >= 4) c.setSegment(CategorieRFMClient.FIDELE);
				        else if (r >= 4 && m >= 4) c.setSegment(CategorieRFMClient.NOUVEAU);
				        else if (r <= 2 && f >= 3 && m >= 3) c.setSegment(CategorieRFMClient.RISQUE);
				        else if (f <= 2 && m <= 2) c.setSegment(CategorieRFMClient.ENDORMI);
				        else c.setSegment(CategorieRFMClient.AUTRES);
				    }
				}


}
