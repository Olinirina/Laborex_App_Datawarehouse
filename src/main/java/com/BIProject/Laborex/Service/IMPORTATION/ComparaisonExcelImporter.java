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

import com.BIProject.Laborex.DAO.ComparaisonBatchService;
import com.BIProject.Laborex.Entity.Article;
import com.BIProject.Laborex.Entity.Comparaison;
import com.BIProject.Laborex.Entity.Concurrent;
import com.BIProject.Laborex.Repository.ArticleRepository;
import com.BIProject.Laborex.Repository.ComparaisonRepository;
import com.BIProject.Laborex.Repository.ConcurrentRepository;

@Service
public class ComparaisonExcelImporter implements ExcelImportation {
	//Pour avoir accès aux tables dans la BD
    @Autowired
    public ArticleRepository articleRepository;
    @Autowired
    public ConcurrentRepository concurrentRepository;
    @Autowired
    public ComparaisonRepository comparaisonRepository;
    @Autowired
    private ComparaisonBatchService comparaisonBatchService;

    //Verifier que le nom du fichier contient "compar"
    @Override
    public boolean supports(String filename) {
        return filename.toLowerCase().contains("compar");
    }

    /*
     * CLE COMPOSITE
     * Comme une comparaison est définie par un article + un concurrent,
     * on crée une petite classe qui représente cette clé unique.
     * */ 
    private static class ComparaisonKey {
        String codeArticle;
        String codeConcurrent;

        public ComparaisonKey(String codeArticle, String codeConcurrent) {
            this.codeArticle = codeArticle;
            this.codeConcurrent = codeConcurrent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ComparaisonKey that = (ComparaisonKey) o;
            return codeArticle.equals(that.codeArticle) &&
                   codeConcurrent.equals(that.codeConcurrent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(codeArticle, codeConcurrent);
        }
    }

    //Logique d'import du fichier EXCEL Comparaison
    @Override
    public void importData(Sheet sheet) throws IOException {
        System.out.println("--- Début de l'importation de Comparaison par lot ---");

        // --- Phase 1: Pré-chargement des données de référence en mémoire ---
        Map<String, Article> articlesExistants = articleRepository.findAll().stream()
                .collect(Collectors.toMap(Article::getCodeArticle, Function.identity()));
        Map<String, Concurrent> concurrentsExistants = concurrentRepository.findAll().stream()
                .collect(Collectors.toMap(Concurrent::getCodeConcurrent, Function.identity()));
        Map<ComparaisonKey, Comparaison> comparaisonsExistantes = comparaisonRepository.findAll().stream()
                .collect(Collectors.toMap(c -> new ComparaisonKey(c.getArticle().getCodeArticle(), c.getConcurrent().getCodeConcurrent()), Function.identity()));
        System.out.println("Pré-chargement terminé.");

        // --- Phase 2: Collecte des entités à créer ou à mettre à jour ---
        
	        // Listes finales pour la sauvegarde par lots
	        List<Article> articlesAMettreAJourOuCreer = new ArrayList<>();
	        List<Concurrent> concurrentsAMettreAJourOuCreer = new ArrayList<>();
	        List<Comparaison> comparaisonsAMettreAJourOuCreer = new ArrayList<>();
	
	        // Caches temporaires pour les nouvelles entités pour éviter les doublons dans le même fichier
	        Map<String, Article> nouveauxArticlesCache = new HashMap<>();
	        Map<String, Concurrent> nouveauxConcurrentsCache = new HashMap<>();
	        
        Row headerRow = null;
        int rowIndex = 0;
        Map<String, Integer> columnIndexMap = new HashMap<>();
        List<String> concurrents = List.of("SOMAPHAR", "DROGEMAD", "COFARMA", "OPHAM", "INTERPHARMA", "MEDICO", "UBI", "SOPHASU", "PHARMALIFE");

        for (Row row : sheet) {
            if (rowIndex == 0) {
                headerRow = row;
                for (Cell cell : headerRow) {
                    columnIndexMap.put(cell.getStringCellValue().trim().toUpperCase(), cell.getColumnIndex());
                }
                rowIndex++;
                continue;
            }

            if (row == null || getStringValue(row.getCell(columnIndexMap.getOrDefault("CODE", -1))).isEmpty()) {
                rowIndex++;
                continue;
            }
            
            String codeArticle = getStringValue(row.getCell(columnIndexMap.get("CODE")));
            String libelleArticle = getStringValue(row.getCell(columnIndexMap.get("LIBELLE")));
            Double prixVente = getNumericValue(row.getCell(columnIndexMap.get("PRIXACTUEL")));

            // Gestion des articles
            Article article = articlesExistants.get(codeArticle);
            if (article == null) {
                article = nouveauxArticlesCache.get(codeArticle);
                if (article == null) {
                    article = new Article();
                    article.setCodeArticle(codeArticle);
                    nouveauxArticlesCache.put(codeArticle, article);
                }
            }
            article.setLibelle(libelleArticle);
            article.setPrixVente(prixVente);
            articlesAMettreAJourOuCreer.add(article);

            // Pour chaque concurrent
            for (String nomConcurrent : concurrents) {
                String nomCol = nomConcurrent.trim().toUpperCase();
                Integer colIndex = columnIndexMap.get(nomCol);
                if (colIndex == null) {
                    continue;
                }

                double prixConcurrent = getNumericValue(row.getCell(colIndex));

                if (prixConcurrent == 0) {
                    continue;
                }

                // Gestion des concurrents
                Concurrent concurrent = concurrentsExistants.get(nomConcurrent);
                if (concurrent == null) {
                    concurrent = nouveauxConcurrentsCache.get(nomConcurrent);
                    if (concurrent == null) {
                        concurrent = new Concurrent();
                        concurrent.setCodeConcurrent(nomConcurrent);
                        concurrent.setNomConcurrent(nomConcurrent);
                        nouveauxConcurrentsCache.put(nomConcurrent, concurrent);
                    }
                }
             // Si le concurrent est nouveau, l'ajouter à la liste de sauvegarde
                if (!concurrentsExistants.containsKey(nomConcurrent) && !concurrentsAMettreAJourOuCreer.contains(concurrent)) {
                    concurrentsAMettreAJourOuCreer.add(concurrent);
                }


                // Gestion de la comparaison
                ComparaisonKey comparaisonKey = new ComparaisonKey(article.getCodeArticle(), concurrent.getCodeConcurrent());
                Comparaison comparaison = comparaisonsExistantes.get(comparaisonKey);

                if (comparaison == null) {
                    comparaison = new Comparaison();
                    comparaison.setArticle(article);
                    comparaison.setConcurrent(concurrent);
                    comparaisonsExistantes.put(comparaisonKey, comparaison);
                }
                
                comparaison.setPrixConcurrent(prixConcurrent);
                
                if (article.getPrixVente() != null && article.getPrixVente() > 0) {
                    double variation = (prixConcurrent - article.getPrixVente()) / article.getPrixVente() * 100;
                    comparaison.setVariation(variation);
                    comparaison.setNouveauPrix(prixConcurrent * 1.1); 
                }
                
                comparaisonsAMettreAJourOuCreer.add(comparaison);
            }
            rowIndex++;
        }

        // --- Phase 3: Insertion finale par lots dans la base de données ---
        
        // Sauvegarde des articles et concurrents
        comparaisonBatchService.insertInBatch(articlesAMettreAJourOuCreer);
        comparaisonBatchService.insertInBatch(concurrentsAMettreAJourOuCreer); 
        
        // Sauvegarde des comparaisons
        comparaisonBatchService.insertInBatch(comparaisonsAMettreAJourOuCreer);

        System.out.println("--- Import terminé. " + comparaisonsAMettreAJourOuCreer.size() + " comparaisons traitées. ---");
    }

 // Méthodes utilitaires
    public double getNumericValue(Cell cell) {
        if (cell == null) return 0;
        
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                    
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case NUMERIC:
                            return cell.getNumericCellValue();
                        case STRING:
                            String stringResult = cell.getStringCellValue();
                            if (stringResult == null || stringResult.trim().isEmpty()) {
                                return 0;
                            }
                            try {
                                return Double.parseDouble(stringResult.replace(",", "."));
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        case ERROR:
                            return 0;
                        default:
                            return 0;
                    }
                    
                case STRING:
                    String stringValue = cell.getStringCellValue();
                    if (stringValue == null || stringValue.trim().isEmpty()) {
                        return 0;
                    }
                    try {
                        return Double.parseDouble(stringValue.replace(",", "."));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                    
                default:
                    return 0;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture de la cellule: " + e.getMessage());
            return 0;
        }
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
    //Methode de traitement de priorité
    @Override
    public int getPriorite() {
        return 3;
    }
}