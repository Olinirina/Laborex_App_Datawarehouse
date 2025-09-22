package com.BIProject.Laborex.Service.ALERTE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.DuckDBConnection;
import com.BIProject.Laborex.Entity.DTO.ALERTE.AlerteDTO;
import com.BIProject.Laborex.Entity.DTO.ALERTE.NiveauSeverite;
import com.BIProject.Laborex.Entity.DTO.ALERTE.TypeAlerte;


@Service
public class StockCritiqueService {
	private static final Logger log = LoggerFactory.getLogger(StockCritiqueService.class);
    
    @Autowired private SeuilCalculService seuilCalculService;
    
    /**
     * Détecte les articles avec un stock critique
     */
    public List<AlerteDTO> detecterStocksCritiques(String search, String sortBy, String order) {
    	List<AlerteDTO> alertesStock= new ArrayList<>();
    	 List<AlerteDTO> articles= new ArrayList<>();
        try {
            // Requête pour récupérer tous les stocks actuels
            // Note: La jointure est corrigée pour être 's.CodeArticle = a.CodeArticle'
            String sql = """
                SELECT s.CodeStock, s.QuantiteStocke, a.CodeArticle, a.LibArticle 
                FROM STOCK s 
                JOIN ARTICLE a ON s.CodeArticle = a.CodeArticle 
                WHERE s.QuantiteStocke > 0
            """;
            
            try (Connection conn = DuckDBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String codeArticle = rs.getString("CodeArticle");
                    String libelle = rs.getString("LibArticle");
                    // Utiliser getInt() pour QuantiteStocke
                    int quantiteActuelle = rs.getInt("QuantiteStocke");
                    
                    // Calculer le seuil critique pour cet article
                    double seuilCritique = seuilCalculService.calculerSeuilCritique(codeArticle);
                    
                    // Vérifier si le stock est en dessous du seuil
                    if (quantiteActuelle <= seuilCritique) {
                        NiveauSeverite severite = seuilCalculService.determinerSeveriteStock(quantiteActuelle, seuilCritique);
                        
                      
                        String description = String.format(
                            "Stocks: %d unités ---- Seuil critique: %.0f unités. " ,
                            quantiteActuelle, seuilCritique
                        );
                       
                        AlerteDTO dto= new AlerteDTO();
                        dto.setCodeReference(codeArticle);
                        dto.setDescription(description);
                        dto.setSeverite(severite);
                        dto.setNomReference(libelle);
                        dto.setType(TypeAlerte.STOCK_CRITIQUE);
                        dto.setValeur(quantiteActuelle);
                        alertesStock.add(dto);
                    }
                    Stream<AlerteDTO> stream = alertesStock.stream();
                  //Filtre de recherche
            	    if (search != null && !search.isEmpty()) {
            	    	//Recherche insensible a la case 
            	        String lowerSearch = search.toLowerCase();
            	        stream = stream.filter(a ->
            	                a.getCodeReference().toLowerCase().contains(lowerSearch) ||
            	                a.getNomReference().toLowerCase().contains(lowerSearch) ||
            	                a.getDescription().toLowerCase().contains(lowerSearch) ||
            	                a.getType().name().toLowerCase().contains(lowerSearch)
            	        );
            	    }

            	    //Construction dynamique du comparateur : Critere de tri
            	    Comparator<AlerteDTO> comparator;
            	    //Choisir sur quelle colonne triée selon SortBy
            	    if ("code".equalsIgnoreCase(sortBy)) {
            	        comparator = Comparator.comparing(AlerteDTO::getCodeReference, String.CASE_INSENSITIVE_ORDER);
            	    } else if ("nom".equalsIgnoreCase(sortBy)) {
            	        comparator = Comparator.comparing(AlerteDTO::getNomReference, String.CASE_INSENSITIVE_ORDER);
            	    }else if ("niveau".equalsIgnoreCase(sortBy)) {
            	        comparator = Comparator.comparing(AlerteDTO::getSeverite);
            	    }  else {
            	    	//Par defaut : triage sur le CA
            	        comparator = Comparator.comparingDouble(AlerteDTO::getValeur);
            	    }

            	    //Gestion de l'ordre
            	    if ("desc".equalsIgnoreCase(order)) {
            	        comparator = comparator.reversed();
            	    }

            	    //Application du tri (Sur stream)
            	    //Materialisation du resultat (Nouvelle liste) sur .collect(Collectors.toList())
            	    articles = stream.sorted(comparator).collect(Collectors.toList());
                   
                }
            }
            
            log.info("Détection des stocks critiques terminée");
            
        } catch (Exception e) {
             log.warn("Erreur lors de la détection des stocks critiques" + e);
        }
        return articles;
        
    }
}
