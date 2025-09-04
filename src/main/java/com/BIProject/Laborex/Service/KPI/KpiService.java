package com.BIProject.Laborex.Service.KPI;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT.KpiPerformanceClient;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT.PerformanceClientDTO;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT.ScatterPoint;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT.TypeClient;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO.KpiPerformanceLabo;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO.PerformanceLaboDTO;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO.ScatterPointLabo;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO.TypeLabo;
import com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION.CategoriePromo;
import com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION.KpiPromotion;
import com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION.RecapPromoDTO;
import com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION.ScatterPointPromotion;
import com.BIProject.Laborex.Entity.DTO.KPI_ROTATION.CategorieRotation;
import com.BIProject.Laborex.Entity.DTO.KPI_ROTATION.FrequenceVente;
import com.BIProject.Laborex.Entity.DTO.KPI_ROTATION.KpiRotation;
import com.BIProject.Laborex.Entity.DTO.KPI_ROTATION.TopArticleRotation;
import com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE.GraphiqueVente;
import com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE.SaisonnaliteDTO;
import com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE.TendanceVenteDTO;
import com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE.TypeVente;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_ARTICLE.ArticleCa;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_ARTICLE.KpiCaArticle;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_ARTICLE.Top5ArticleCa;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT.ClientCa;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT.KpiCaClient;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT.Top5ClientCa;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_LABO.KpiCaLabo;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_LABO.LaboCa;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_LABO.Top5LaboCa;


@Service
public class KpiService {
	//========================CA PAR ARTICLE=========================
	public KpiCaArticle getCaParArticle(String search, String sortBy, String order) {
	    KpiCaArticle kpiCaArticle = new KpiCaArticle();

	    String sql = """
	            SELECT a.CodeArticle, a.LibArticle, SUM(v.Montant_Vente) AS CA
	            FROM Vente v
	            JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle
	            GROUP BY a.CodeArticle, a.LibArticle
	            """;

	    List<ArticleCa> temp = new ArrayList<>();
	    double caGlobal = 0.0;

	    try (Connection conn = DuckDBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            ArticleCa article = new ArticleCa();
	            article.setCodeArticle(rs.getString("CodeArticle"));
	            article.setLibArticle(rs.getString("LibArticle"));
	            article.setCa(rs.getDouble("CA"));
	            temp.add(article);
	            caGlobal += article.getCa();
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // Calcul des pourcentages
	    for (ArticleCa article : temp) {
	        double pourcentage = (caGlobal > 0) ? (article.getCa() / caGlobal) * 100 : 0;
	        article.setPourcentage(Math.round(pourcentage * 100.0) / 100.0);
	    }

	    //Le top 5 reste basé sur TOUTES les données
	    List<Top5ArticleCa> top5 = temp.stream()
	            .sorted(Comparator.comparingDouble(ArticleCa::getCa).reversed()) // Trier la liste par CA decroissant
	            .limit(5) // Prendre les 5 premiers
	            .map(a -> new Top5ArticleCa(a.getLibArticle(), a.getPourcentage()))// Convertir en TopArticles
	            .collect(Collectors.toList());

	    // Application du filtre + tri uniquement sur le tableau
	    //Creer un flux (pipeline) pour enchainer les filtres et tri sans modifier la liste d'origine
	    Stream<ArticleCa> stream = temp.stream();

	    //Filtre de recherche
	    if (search != null && !search.isEmpty()) {
	    	//Recherche insensible a la case 
	        String lowerSearch = search.toLowerCase();
	        stream = stream.filter(a ->
	                a.getCodeArticle().toLowerCase().contains(lowerSearch) ||
	                a.getLibArticle().toLowerCase().contains(lowerSearch)
	        );
	    }

	    //Construction dynamique du comparateur : Critere de tri
	    Comparator<ArticleCa> comparator;
	    //Choisir sur quelle colonne triée selon SortBy
	    if ("codeArticle".equalsIgnoreCase(sortBy)) {
	        comparator = Comparator.comparing(ArticleCa::getCodeArticle, String.CASE_INSENSITIVE_ORDER);
	    } else if ("libArticle".equalsIgnoreCase(sortBy)) {
	        comparator = Comparator.comparing(ArticleCa::getLibArticle, String.CASE_INSENSITIVE_ORDER);
	    } else {
	    	//Par defaut : triage sur le CA
	        comparator = Comparator.comparingDouble(ArticleCa::getCa);
	    }

	    //Gestion de l'ordre
	    if ("desc".equalsIgnoreCase(order)) {
	        comparator = comparator.reversed();
	    }

	    //Application du tri (Sur stream)
	    //Materialisation du resultat (Nouvelle liste) sur .collect(Collectors.toList())
	    List<ArticleCa> articles = stream.sorted(comparator).collect(Collectors.toList());

	    // Remplir le DTO final
	    kpiCaArticle.setCaGlobal(Math.round(caGlobal * 100.0) / 100.0);
	    kpiCaArticle.setArticles(articles);
	    kpiCaArticle.setTop5(top5);

	    return kpiCaArticle;
	}

	
	//========================CA PAR CLIENT=========================
	public KpiCaClient getCaParClient(String search, String sortBy, String order) {
		KpiCaClient kpiCaClient= new KpiCaClient();
		
		
		String sql = "SELECT c.CodeCli, c.NomCli, SUM(v.Montant_Vente) AS CA " +
                "FROM Vente v " +
                "JOIN CLIENT c ON v.CodeClient= c.CodeCli " +
                "GROUP BY c.CodeCli, c.NomCli";

	    try (Connection conn = DuckDBConnection.getConnection();
		         Statement stmt = conn.createStatement();
		         ResultSet rs = stmt.executeQuery(sql)) {

		        double caGlobal = 0.0;
		        List<ClientCa> temp = new ArrayList<>();
		        while (rs.next()) {
		        	ClientCa client = new ClientCa();
		            client.setCodeClient(rs.getString("CodeCli"));
		            client.setNomClient(rs.getString("NomCli"));
		            client.setCa(rs.getDouble("CA"));
		            
		            temp.add(client);
		            caGlobal += client.getCa();
		        }

		        for (ClientCa client : temp) {
		            double pourcentage = (caGlobal > 0) ? (client.getCa() / caGlobal) * 100 : 0;
		            client.setPourcentage(Math.round(pourcentage * 100.0) / 100.0);
		            
		        }

		        // Top 5
		        List<Top5ClientCa> top5 = temp.stream()
		                .sorted(Comparator.comparingDouble(ClientCa::getCa).reversed())
		                .limit(5)
		                .map(a -> new Top5ClientCa(a.getNomClient(), a.getPourcentage()))
		                .collect(Collectors.toList());

		        //Filtrage et triage
		        //Flux
		        Stream<ClientCa> stream = temp.stream();
		        //Filtre de recherche
		        if(search !=null && !search.isEmpty()) {
		        	String lowerSearch= search.toLowerCase();
		        	stream = stream.filter(c ->
		        		c.getCodeClient().toLowerCase().contains(lowerSearch) ||
		        		c.getNomClient().toLowerCase().contains(lowerSearch)
		        	);
		        }
		        
		        Comparator<ClientCa> comparator;
		        if("codeClient".equalsIgnoreCase(sortBy)) {
		        	comparator= Comparator.comparing(ClientCa::getCodeClient,String.CASE_INSENSITIVE_ORDER);
		        }else  if("nomClient".equalsIgnoreCase(sortBy)) {
		        	comparator= Comparator.comparing(ClientCa::getNomClient,String.CASE_INSENSITIVE_ORDER);
		        }else {
		        	comparator= Comparator.comparing(ClientCa::getCa);
		        }
		        
		        //Ordre
		        if("desc".equalsIgnoreCase(order)) {
		        	comparator= comparator.reversed();
		        }
		        //Application du tri
		        List<ClientCa> clients = stream.sorted(comparator).collect(Collectors.toList());
		        
		        kpiCaClient.setCaGlobal(Math.round(caGlobal * 100.0) / 100.0);
		        kpiCaClient.setClients(clients);
		        kpiCaClient.setTop5(top5);

		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	    
		return kpiCaClient;
	}
	
	//========================CA PAR LABO=========================
		public KpiCaLabo getCaParLabo(String search, String sortBy, String order) {
			KpiCaLabo kpiCalabo= new KpiCaLabo();
			
			
			String sql = "SELECT l.CodeLabo, l.NomLabo, SUM(v.Montant_Vente) AS CA " +
	                "FROM Vente v " +
	                "JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle " +
	                "JOIN LABO l on a.CodeLabo= l.CodeLabo " +
	                "GROUP BY l.CodeLabo, l.NomLabo";

		    try (Connection conn = DuckDBConnection.getConnection();
			         Statement stmt = conn.createStatement();
			         ResultSet rs = stmt.executeQuery(sql)) {

			        double caGlobal = 0.0;
			        List<LaboCa> temp = new ArrayList<>();
			        while (rs.next()) {
			        	LaboCa labo = new LaboCa();
			        	labo.setCodeLabo(rs.getString("CodeLabo"));
			        	labo.setNomLabo(rs.getString("NomLabo"));
			            labo.setCa(rs.getDouble("CA"));
			            
			            temp.add(labo);
			            caGlobal += labo.getCa();
			        }

			        for (LaboCa labo : temp) {
			            double pourcentage = (caGlobal > 0) ? (labo.getCa() / caGlobal) * 100 : 0;
			            labo.setPourcentage(Math.round(pourcentage * 100.0) / 100.0);
			            
			        }

			        // Top 5
			        List<Top5LaboCa> top5 = temp.stream()
			                .sorted(Comparator.comparingDouble(LaboCa::getCa).reversed())
			                .limit(5)
			                .map(a -> new Top5LaboCa(a.getNomLabo(), a.getPourcentage()))
			                .collect(Collectors.toList());

			      //Filtrage et triage
			        //Flux
			        Stream<LaboCa> stream = temp.stream();
			        //Filtre de recherche
			        if(search !=null && !search.isEmpty()) {
			        	String lowerSearch= search.toLowerCase();
			        	stream = stream.filter(c ->
			        		c.getCodeLabo().toLowerCase().contains(lowerSearch) ||
			        		c.getNomLabo().toLowerCase().contains(lowerSearch)
			        	);
			        }
			        
			        Comparator<LaboCa> comparator;
			        if("codeLabo".equalsIgnoreCase(sortBy)) {
			        	comparator= Comparator.comparing(LaboCa::getCodeLabo,String.CASE_INSENSITIVE_ORDER);
			        }else  if("nomLabo".equalsIgnoreCase(sortBy)) {
			        	comparator= Comparator.comparing(LaboCa::getNomLabo,String.CASE_INSENSITIVE_ORDER);
			        }else {
			        	comparator= Comparator.comparing(LaboCa::getCa);
			        }
			        
			        //Ordre
			        if("desc".equalsIgnoreCase(order)) {
			        	comparator= comparator.reversed();
			        }
			        //Application du tri
			        List<LaboCa> labos = stream.sorted(comparator).collect(Collectors.toList());
			        
			        kpiCalabo.setCaGlobal(Math.round(caGlobal * 100.0) / 100.0);
			        kpiCalabo.setLabos(labos);
			        kpiCalabo.setTop5(top5);

			    } catch (SQLException e) {
			        e.printStackTrace();
			    }
		    
			return kpiCalabo;
		}
		//========================ROTATION=========================
		public KpiRotation getKpiRotation(String search, String sortBy, String order) {
			KpiRotation kpiRotation= new KpiRotation();
			
			String sql = "SELECT a.CodeArticle ,a.LibArticle, COUNT(v.CodeVente) AS nombreDeVentes " +
	                "FROM Vente v " +
	                "JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle " +
	                "GROUP BY a.CodeArticle ,a.LibArticle";

		    try (Connection conn = DuckDBConnection.getConnection();
			         Statement stmt = conn.createStatement();
			         ResultSet rs = stmt.executeQuery(sql)) {
		    	
			        List<FrequenceVente> temp = new ArrayList<>();
			        while (rs.next()) {
			        	FrequenceVente vente = new FrequenceVente();
			        	vente.setCodeArticle(rs.getString("CodeArticle"));
			        	vente.setLibelleArticle(rs.getString("LibArticle"));
			        	vente.setNombreDeVentes(rs.getLong("nombreDeVentes"));		            
			            temp.add(vente);
			        }

			        if (!temp.isEmpty()) {
			        	long maxVentes = temp.stream()
			                     .mapToLong(FrequenceVente::getNombreDeVentes)
			                     .max()
			                     .orElse(1); // éviter division par zéro 
			        for (FrequenceVente vente : temp) {
			            long nbVente= vente.getNombreDeVentes();
			            // Catégorisation basée sur un ratio par rapport au top article
		                double ratio = (double) nbVente / maxVentes;
		                if (ratio >= 0.75) {
		                    vente.setCategorie(CategorieRotation.ROTATION_ELEVEE);
		                } else if (ratio >= 0.25) {
		                	vente.setCategorie(CategorieRotation.ROTATION_MOYENNE);
		                } else {
		                	vente.setCategorie(CategorieRotation.ROTATION_FAIBLE);
		                }
			          
			        }

			        // Top 5
			        List<TopArticleRotation> top5 = temp.stream()
			                .sorted(Comparator.comparingDouble(FrequenceVente::getNombreDeVentes).reversed())
			                .limit(5)
			                .map(a -> new TopArticleRotation(a.getLibelleArticle(), a.getNombreDeVentes()))
			                .collect(Collectors.toList());
			        
			      //Filtrage et triage
			        //Flux
			        Stream<FrequenceVente> stream = temp.stream();
			        //Filtre de recherche
			        if(search !=null && !search.isEmpty()) {
			        	String lowerSearch= search.toLowerCase();
			        	stream = stream.filter(c ->
			        		c.getCodeArticle().toLowerCase().contains(lowerSearch) ||
			        		c.getLibelleArticle().toLowerCase().contains(lowerSearch) ||
			        		c.getCategorie().name().toLowerCase().contains(lowerSearch)
			        	);
			        }
			        
			        Comparator<FrequenceVente> comparator;
			        if("codeArticle".equalsIgnoreCase(sortBy)) {
			        	comparator= Comparator.comparing(FrequenceVente::getCodeArticle,String.CASE_INSENSITIVE_ORDER);
			        }else  if("nom".equalsIgnoreCase(sortBy)) {
			        	comparator= Comparator.comparing(FrequenceVente::getLibelleArticle,String.CASE_INSENSITIVE_ORDER);
			        }else  if("categorie".equalsIgnoreCase(sortBy)) {
			        	comparator= Comparator.comparing(FrequenceVente::getCategorie);
			        }
			        else {
			        	comparator= Comparator.comparing(FrequenceVente::getNombreDeVentes);
			        }
			        
			        //Ordre
			        if("desc".equalsIgnoreCase(order)) {
			        	comparator= comparator.reversed();
			        }
			        //Application du tri
			        List<FrequenceVente> ventes = stream.sorted(comparator).collect(Collectors.toList());
			        

			        kpiRotation.setVentes(ventes);
			        kpiRotation.setTop5(top5);

			    }
			     else {
		            System.out.println("Aucune donnée de vente disponible");
		        } 
			        
			   }catch (SQLException e) {
			        e.printStackTrace();
			}
			
			return kpiRotation;
		    }

		//========================= PERFORMANCE CLIENT =========================
		public KpiPerformanceClient getKpiPerformanceClients(String search, String sortBy, String order) {
		    KpiPerformanceClient kpi = new KpiPerformanceClient();		  
		    List<ScatterPoint> scatterData = new ArrayList<>();

		    String sql = """
		        SELECT c.CodeCli, c.NomCli, SUM(v.Montant_Vente) AS ChiffreAffaires,
		               COUNT(v.CodeVente) AS FrequenceAchat,
		               MAX(d.DateValue) AS DerniereCommande
		        FROM Vente v
		        JOIN CLIENT c ON v.CodeClient = c.CodeCli
		        JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
		        GROUP BY c.CodeCli, c.NomCli
		    """;
		  
		    try (Connection conn = DuckDBConnection.getConnection();
		         Statement stmt = conn.createStatement();
		         ResultSet rs = stmt.executeQuery(sql)) {

		        double totalCA = 0;
		        List<PerformanceClientDTO> temp = new ArrayList<>();

		        // Charger les données
		        while (rs.next()) {
		            java.sql.Date sqlDate = rs.getDate("DerniereCommande");
		            LocalDate derniereCommande = (sqlDate != null) ? sqlDate.toLocalDate() : null;

		            String codeClient= rs.getString("CodeCli");
		            String nomClient= rs.getString("NomCli");
		            double ca = rs.getDouble("ChiffreAffaires");
		            long freq = rs.getLong("FrequenceAchat");

		            temp.add(new PerformanceClientDTO(
		            	codeClient,
		                nomClient,
		                ca,
		                freq,
		                derniereCommande,
		                null
		            ));
		            totalCA += ca;
		        }

		        // Déterminer la meilleure référence pour calcul
		        if (!temp.isEmpty()) {
		            double maxCA = temp.stream().mapToDouble(PerformanceClientDTO::getChiffreAffaires).max().orElse(1);

		            for (PerformanceClientDTO client : temp) {
		                double ratioCA = client.getChiffreAffaires() / maxCA;

		                // Attribution de la catégorie
		                if (ratioCA >= 0.75 && client.getFrequenceAchat() > 5) {
		                    client.setCategorie(TypeClient.VIP);
		                } else if (ratioCA >= 0.25 || client.getFrequenceAchat() > 2) {
		                    client.setCategorie(TypeClient.REGULIER);
		                } else {
		                    client.setCategorie(TypeClient.OCCASIONNEL);
		                }

		                // Ajout pour le scatter plot
		                double pctCA = (totalCA > 0) ? (client.getChiffreAffaires() / totalCA) * 100 : 0;
		                scatterData.add(new ScatterPoint(client.getFrequenceAchat(), pctCA));

		               
		            }  
		        }
		      //Filtrage et triage
		        //Flux
		        Stream<PerformanceClientDTO> stream = temp.stream();
		        //Filtre de recherche
		        if(search !=null && !search.isEmpty()) {
		        	String lowerSearch= search.toLowerCase();
		        	stream = stream.filter(c ->
		        		c.getCodeClient().toLowerCase().contains(lowerSearch) ||
		        		c.getNomClient().toLowerCase().contains(lowerSearch) ||
		        		c.getCategorie().name().toLowerCase().contains(lowerSearch)
		        	);
		        }
		        
		        Comparator<PerformanceClientDTO> comparator;
		        if("code".equalsIgnoreCase(sortBy)) {
		        	comparator= Comparator.comparing(PerformanceClientDTO::getCodeClient,String.CASE_INSENSITIVE_ORDER);
		        }else  if("nom".equalsIgnoreCase(sortBy)) {
		        	comparator= Comparator.comparing(PerformanceClientDTO::getNomClient,String.CASE_INSENSITIVE_ORDER);
		        }else if("categorie".equalsIgnoreCase(sortBy)) {
		        	comparator= Comparator.comparing(PerformanceClientDTO::getCategorie);
		        } else {
		        	comparator= Comparator.comparing(PerformanceClientDTO::getChiffreAffaires);
		        }
		        
		        //Ordre
		        if("desc".equalsIgnoreCase(order)) {
		        	comparator= comparator.reversed();
		        }
		        //Application du tri
		        List<PerformanceClientDTO> clients = stream.sorted(comparator).collect(Collectors.toList());

		        kpi.setClients(clients);
		        kpi.setScatterData(scatterData);

		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

		    return kpi;
		}
		
		//========================= PERFORMANCE LABO =========================
				public KpiPerformanceLabo getKpiPerformanceLabo(String search, String sortBy, String order) {
					KpiPerformanceLabo kpi = new KpiPerformanceLabo();
				    List<ScatterPointLabo> scatterData = new ArrayList<>();

				    String sql = """			    		
				    		SELECT l.CodeLabo , l.NomLabo,
				                SUM(v.Montant_Vente) AS ChiffreAffaires,
				                COUNT(DISTINCT a.CodeArticle) AS NombreArticles
				            FROM VENTE v
				            JOIN ARTICLE a ON v.CodeArticle = a.CodeArticle
				            JOIN LABO l ON a.CodeLabo = l.CodeLabo
				            GROUP BY l.CodeLabo,l.NomLabo
				    """;
				    try (Connection conn = DuckDBConnection.getConnection();
				         Statement stmt = conn.createStatement();
				         ResultSet rs = stmt.executeQuery(sql)) {

				        double totalCA = 0;
				        List<PerformanceLaboDTO> temp = new ArrayList<>();

				        // Charger les données
				        while (rs.next()) {

				            String codeLabo= rs.getString("CodeLabo");
				            String nomLabo= rs.getString("NomLabo");
				            double ca = rs.getDouble("ChiffreAffaires");
				            long nbArt = rs.getLong("NombreArticles");

				            temp.add(new PerformanceLaboDTO(
				            		codeLabo,
				            		nomLabo,
				            		ca,
				            		nbArt,
				            		null
				            ));
				            totalCA += ca;
				        }

				     // Calculer et ajouter l'interprétation pour chaque laboratoire
				        if (!temp.isEmpty()) {
				            double maxChiffreAffaires= temp.get(0).getChiffreAffaires();
				            long maxArticles= temp.stream()
				            		.mapToLong(PerformanceLaboDTO::getNombreArticles)
				            		.max()
				            		.orElse(0);
				            for(PerformanceLaboDTO labo : temp) {
				            	double chiffreAffaires= labo.getChiffreAffaires();
				            	double nombreArticles= labo.getNombreArticles();
				            	//ratio
				            	double ratioCA= chiffreAffaires/ maxChiffreAffaires;
				            	double ratioArticles =(double) nombreArticles/maxArticles;
				            	if (ratioCA >= 0.75 && ratioArticles >= 0.5) {
				                    labo.setCategorie(TypeLabo.STRATEGIQUE);
				                } else if (ratioCA >= 0.25 && ratioArticles >= 0.25) {
				                	labo.setCategorie(TypeLabo.FIABLE);
				                } else {
				                	labo.setCategorie(TypeLabo.NICHE);
				                }
				            	 // Ajout pour le scatter plot
				                double pctCA = (totalCA > 0) ? (labo.getChiffreAffaires() / totalCA) * 100 : 0;
				                scatterData.add(new ScatterPointLabo(labo.getNombreArticles(), pctCA));
				            }
				        }
				      //Filtrage et triage
				        //Flux
				        Stream<PerformanceLaboDTO> stream = temp.stream();
				        //Filtre de recherche
				        if(search !=null && !search.isEmpty()) {
				        	String lowerSearch= search.toLowerCase();
				        	stream = stream.filter(c ->
				        		c.getCodeLabo().toLowerCase().contains(lowerSearch) ||
				        		c.getNomLabo().toLowerCase().contains(lowerSearch) ||
				        		c.getCategorie().name().toLowerCase().contains(lowerSearch)
				        	);
				        }
				        
				        Comparator<PerformanceLaboDTO> comparator;
				        if("code".equalsIgnoreCase(sortBy)) {
				        	comparator= Comparator.comparing(PerformanceLaboDTO::getCodeLabo,String.CASE_INSENSITIVE_ORDER);
				        }else  if("nom".equalsIgnoreCase(sortBy)) {
				        	comparator= Comparator.comparing(PerformanceLaboDTO::getNomLabo,String.CASE_INSENSITIVE_ORDER);
				        }else if("categorie".equalsIgnoreCase(sortBy)) {
				        	comparator= Comparator.comparing(PerformanceLaboDTO::getCategorie);
				        } else {
				        	comparator= Comparator.comparing(PerformanceLaboDTO::getChiffreAffaires);
				        }
				        
				        //Ordre
				        if("desc".equalsIgnoreCase(order)) {
				        	comparator= comparator.reversed();
				        }
				        //Application du tri
				        List<PerformanceLaboDTO> labos = stream.sorted(comparator).collect(Collectors.toList());

				        kpi.setLabos(labos);
				        kpi.setScatterData(scatterData);


				    } catch (SQLException e) {
				        e.printStackTrace();
				    }

				    return kpi;
				}
				
				//========================= SAISONNALITE DES VENTES =========================
				public SaisonnaliteDTO getSaisonnalite(String search, String sortBy, String order) {
					SaisonnaliteDTO tendances= new SaisonnaliteDTO();
					List<GraphiqueVente> graphiques= new ArrayList<>();
					String sql = """
				            SELECT
				                d.Annee,
				                d.Mois,
				                SUM(v.Montant_Vente) AS ChiffreAffaires
				            FROM VENTE v
				            JOIN DATE_PERSO d ON v.CodeDate = d.CodeDate
				            GROUP BY
				                d.Annee,
				                d.Mois
				            """;
		
				    try (Connection conn = DuckDBConnection.getConnection();
					         Statement stmt = conn.createStatement();
					         ResultSet rs = stmt.executeQuery(sql)) {

				    	double caGlobal= 0.0;
				    	List<TendanceVenteDTO> temp= new ArrayList<>();
					        // Étape 1 : Remplir la liste avec les données brutes
					        while (rs.next()) {
					           TendanceVenteDTO vente= new TendanceVenteDTO();
					           vente.setAnnee(rs.getInt("Annee"));
					           vente.setMois(rs.getInt("Mois"));
					           vente.setChiffreAffaires(rs.getDouble("ChiffreAffaires"));
					           temp.add(vente);
					           caGlobal += vente.getChiffreAffaires();
					        }
					        for (TendanceVenteDTO v : temp) {
					            double pourcentage = (caGlobal > 0) ? (v.getChiffreAffaires() / caGlobal) * 100 : 0;
					            v.setPourcentage(Math.round(pourcentage * 100.0) / 100.0);
					            
					        // Analyser et ajouter l'interprétation
					        if (!temp.isEmpty()) {
					            // Calculer le chiffre d'affaires moyen sur l'ensemble de la période
					            double chiffreAffairesTotal = temp.stream()
					                                                      .mapToDouble(TendanceVenteDTO::getChiffreAffaires)
					                                                      .sum();
					            double chiffreAffairesMoyen = chiffreAffairesTotal / temp.size();
					            double chiffreAffaires = v.getChiffreAffaires();
					                

					                if (chiffreAffaires > chiffreAffairesMoyen * 1.2) { // 20% au-dessus de la moyenne
					                    v.setCategorie(TypeVente.FORTE);
					                } else if (chiffreAffaires < chiffreAffairesMoyen * 0.8) { // 20% en dessous de la moyenne
					                    v.setCategorie(TypeVente.FAIBLE);
					                } else {
					                	v.setCategorie(TypeVente.STABLE);;
					                }
					              
					                
					              //Graphiques
					                GraphiqueVente gv= new GraphiqueVente();
					                String mois= Month.of(v.getMois()).name();
					                gv.setMois(mois);
					                gv.setPourcentage(v.getPourcentage());
					                graphiques.add(gv);

					               	}
					        } 
					      //Filtrage et triage
					        //Flux
					        Stream<TendanceVenteDTO> stream = temp.stream();
					        //Filtre de recherche
					        if (search != null && !search.isEmpty()) {
					            String lowerSearch = search.toLowerCase();
					            stream = stream.filter(c ->
					                String.valueOf(c.getMois()).contains(lowerSearch) ||  // convertir int en String
					                c.getCategorie().name().toLowerCase().contains(lowerSearch) // récupérer le nom de l'enum
					            );
					        }
					        
					        Comparator<TendanceVenteDTO> comparator;
					        if("annee".equalsIgnoreCase(sortBy)) {
					        	comparator= Comparator.comparing(TendanceVenteDTO::getAnnee);
					        }else  if("mois".equalsIgnoreCase(sortBy)) {
					        	comparator= Comparator.comparing(TendanceVenteDTO::getMois);
					        }else if("categorie".equalsIgnoreCase(sortBy)) {
					        	comparator= Comparator.comparing(TendanceVenteDTO::getCategorie);
					        } else {
					        	comparator= Comparator.comparing(TendanceVenteDTO::getChiffreAffaires);
					        }
					        
					        //Ordre
					        if("desc".equalsIgnoreCase(order)) {
					        	comparator= comparator.reversed();
					        }
					        //Application du tri
					        List<TendanceVenteDTO> ventes = stream.sorted(comparator).collect(Collectors.toList());
					        
					        tendances.setGraphiqueVentes(graphiques);
					        tendances.setTendances(ventes);
					        

					    } catch (SQLException e) {
					        e.printStackTrace();
					    }
					return tendances;
				}
				//========================= IMPACT PROMOTION SUR LES VENTES =========================

				public KpiPromotion getImpactPromo(String search, String sortBy, String order) {
					KpiPromotion kpi= new KpiPromotion();
					List<ScatterPointPromotion> scatterData= new ArrayList<>();
					String sql = """
					        SELECT
					            p.CodePromo ,p.NomPromo, p.TypePromo, 
					            SUM(p.UgLivre) AS ugLivre,
					            SUM(v.Montant_Vente) AS ChiffreAffaires
					        FROM VENTE v
					        JOIN PROMOTION p ON v.CodePromo = p.CodePromo
					        GROUP BY p.CodePromo ,p.NomPromo, p.TypePromo
					    """;


				    try (Connection conn = DuckDBConnection.getConnection();
				         Statement stmt = conn.createStatement();
				         ResultSet rs = stmt.executeQuery(sql)) {

				    	double caGlobal=0.0;
				    	List<RecapPromoDTO> temp= new ArrayList<>();
				        while (rs.next()) {
				            RecapPromoDTO promo= new RecapPromoDTO();
				            promo.setCodePromo(rs.getString("CodePromo"));
				            promo.setNomPromo(rs.getString("NomPromo"));
				            promo.setTypePromo(rs.getString("TypePromo"));
				            promo.setUg(rs.getInt("ugLivre"));
				            promo.setChiffreAffaire(rs.getDouble("ChiffreAffaires"));
				            temp.add(promo);
				            caGlobal += promo.getChiffreAffaire();
				        }

				        // Étape 2 : Analyser et ajouter l'interprétation
				        if (!temp.isEmpty()) {
				            double maxChiffreAffaires = temp.get(0).getChiffreAffaire();
				            long maxUgLivre = temp.get(0).getUg();;

				            for (RecapPromoDTO promo : temp) {
				                double ratioCA = promo.getChiffreAffaire() / maxChiffreAffaires;
				                double ratioUg = (double) promo.getUg() / maxUgLivre;
				                //pourcentage du ca 
				                double pourcentage = (caGlobal > 0) ? (promo.getChiffreAffaire() / caGlobal) * 100 : 0;
					            promo.setPourcentageCA(Math.round(pourcentage * 100.0) / 100.0);

				                if (ratioCA >= 0.75 && ratioUg >= 0.75) {
				                    promo.setCategorie(CategoriePromo.SUCCES);
				                } else if (ratioCA >= 0.5 && ratioUg < 0.5) {
				                	promo.setCategorie(CategoriePromo.MARGE);
				                } else if (ratioCA < 0.5 && ratioUg >= 0.5) {
				                	promo.setCategorie(CategoriePromo.VOLUMINEUSE);
				                } else {
				                	promo.setCategorie(CategoriePromo.A_OPTIMISER);
				                }
				          	
				                
				                //Scatter
				                ScatterPointPromotion scatter= new ScatterPointPromotion();
				                scatter.setPourcentageCA(pourcentage);
				                scatter.setUg(promo.getUg());
				                scatterData.add(scatter);
				                
				            }
				            //Filtrage et triage
					        //Flux
					        Stream<RecapPromoDTO> stream = temp.stream();
					        //Filtre de recherche
					        if (search != null && !search.isEmpty()) {
					            String lowerSearch = search.toLowerCase();
					            stream = stream.filter(c ->
					                c.getCodePromo().contains(lowerSearch) || 
					                c.getNomPromo().contains(lowerSearch) || 
					                c.getTypePromo().contains(lowerSearch) || 
					                c.getCategorie().name().toLowerCase().contains(lowerSearch) 
					            );
					        }
					        
					        Comparator<RecapPromoDTO> comparator;
					        if("code".equalsIgnoreCase(sortBy)) {
					        	comparator= Comparator.comparing(RecapPromoDTO::getCodePromo,String.CASE_INSENSITIVE_ORDER);
					        }else  if("nom".equalsIgnoreCase(sortBy)) {
					        	comparator= Comparator.comparing(RecapPromoDTO::getNomPromo,String.CASE_INSENSITIVE_ORDER);
					        }else if("categorie".equalsIgnoreCase(sortBy)) {
					        	comparator= Comparator.comparing(RecapPromoDTO::getCategorie);
					        }else  if("type".equalsIgnoreCase(sortBy)) {
					        	comparator= Comparator.comparing(RecapPromoDTO::getTypePromo,String.CASE_INSENSITIVE_ORDER);
					        } else {
					        	comparator= Comparator.comparing(RecapPromoDTO::getUg);
					        }
					        
					        //Ordre
					        if("desc".equalsIgnoreCase(order)) {
					        	comparator= comparator.reversed();
					        }
					        //Application du tri
					        List<RecapPromoDTO> promotions = stream.sorted(comparator).collect(Collectors.toList());
				            kpi.setPromos(promotions);
				            kpi.setScatterData(scatterData);    	
				        }

				    } catch (SQLException e) {
				        e.printStackTrace();
				    }
				    return kpi;
				}
				
				
}
