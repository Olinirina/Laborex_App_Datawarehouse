package com.BIProject.Laborex.Service.IMPORTATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.StockBatchService;
import com.BIProject.Laborex.Entity.Article;
import com.BIProject.Laborex.Entity.Stock;
import com.BIProject.Laborex.Repository.ArticleRepository;
import com.BIProject.Laborex.Repository.StockRepository;

@Service
public class StockExcelImporter implements ExcelImportation {

    @Autowired private ArticleRepository articleRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private StockBatchService stockBatchService;

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().contains("stock");
    }
    
    private static class StockKey{
    	private final String codeArticle;
    	public StockKey(String codeArticle) {
    		this.codeArticle= codeArticle;
    	}
    	
    	@Override
    	public boolean equals(Object o) {
    		if(this == o) return true;
    		if(o == null || getClass() != o.getClass()) return false;
    		StockKey that= (StockKey) o;
    		return codeArticle.equals(that.codeArticle);
    	}
    	
    	@Override
    	public int hashCode() {
    		return Objects.hash(codeArticle);
    	}
    }

    @Override
    public void importData(Sheet sheet) throws IOException {
        System.out.println("--- Début de l'importation de Stock par lot ---");

        // Phase 1 : Pré-chargement des données de référence en mémoire
        Map<String, Article> articlesExistants = articleRepository.findAll().stream()
                .collect(Collectors.toMap(Article::getCodeArticle, Function.identity()));
        Map<StockKey, Stock> stocksExistants = stockRepository.findAll().stream()
                .collect(Collectors.toMap(stock -> new StockKey(stock.getArticle().getCodeArticle()), Function.identity()));
        System.out.println("Pré-chargement terminé. " + articlesExistants.size() + " articles trouvés.");

        // Phase 2 : Collecte des entités à créer ou à mettre à jour
        List<Article> articlesAMettreAJour = new ArrayList<>();
        List<Stock> stocksAMettreAJourOuACreer = new ArrayList<>();
        // Caches temporaires pour les nouvelles entités pour éviter les doublons dans le même fichier
        Map<String, Article> nouveauxArticlesCache = new HashMap<>();

        Row headerRow = null;
        int rowIndex = 0;
        Map<String, Integer> columnIndexMap = new HashMap<>();

        for (Row row : sheet) {
            if (rowIndex == 0) {
                headerRow = row;
                for (Cell cell : headerRow) {
                    columnIndexMap.put(cell.getStringCellValue().trim().toUpperCase(), cell.getColumnIndex());
                }
                rowIndex++;
                continue;
            }

            if (row == null || row.getCell(0) == null) {
                rowIndex++;
                continue;
            }

            // Lecture des valeurs
            String codeArticle = getStringValue(row.getCell(columnIndexMap.getOrDefault("NOART", -1)));
            String libelleArticle = getStringValue(row.getCell(columnIndexMap.getOrDefault("LIBART", -1)));
            Integer quantiteStocke = (int) getNumericValue(row.getCell(columnIndexMap.getOrDefault("STOCK", -1)));
            Integer coursRoute = (int) getNumericValue(row.getCell(columnIndexMap.getOrDefault("CROUTE", -1)));

            // Gestion des articles 
            Article article = articlesExistants.get(codeArticle);
            if (article == null) {
                article= nouveauxArticlesCache.get(codeArticle);
                if(article == null) {
                	article = new Article();
                	article.setCodeArticle(codeArticle);
                	article.setLibelle(libelleArticle);              	
                }
                articlesAMettreAJour.add(article);
            }

            //Gestion des stocks
            StockKey stockKey= new StockKey(article.getCodeArticle());
            Stock stock= stocksExistants.get(stockKey);
            if(stock == null) {
            	stock= new Stock();
            	stock.setArticle(article);
            	stock.setCoursRoute(coursRoute);
            	stock.setQuantiteStocke(quantiteStocke);
            	stocksExistants.put(stockKey, stock);
            }
            stocksAMettreAJourOuACreer.add(stock);
            rowIndex++;
        }

        // Phase 3 : Insertion/mise à jour finale par lots
        
        //Sauvegarde
        stockBatchService.insertInBatch(articlesAMettreAJour);
        stockBatchService.insertInBatch(stocksAMettreAJourOuACreer);
        
        System.out.println("--- Import de stocks terminé. " + stocksAMettreAJourOuACreer.size() + " stocks mis à jour/créés. ---");
    }

    // Méthodes utilitaires
    public double getNumericValue(Cell cell) {
        if (cell == null) return 0;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().replace(",", "."));
            } catch (NumberFormatException e) {
                return 0; // ou logger une erreur
            }
        }
        return 0;
    }

    public String getStringValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return "";
    }
    
    @Override
    public int getPriorite() {
        return 1;
    }
}
