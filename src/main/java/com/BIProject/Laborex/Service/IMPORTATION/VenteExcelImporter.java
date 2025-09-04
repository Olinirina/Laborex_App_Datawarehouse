package com.BIProject.Laborex.Service.IMPORTATION;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BIProject.Laborex.DAO.VenteBatchService;
import com.BIProject.Laborex.Entity.*;
import com.BIProject.Laborex.Repository.*;

@Service
public class VenteExcelImporter implements ExcelImportation {

    @Autowired private DateRepository dateRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private LaboRepository laboRepository;
    @Autowired private PromotionRepository promoRepository;
    @Autowired private VenteRepository venteRepository;
    @Autowired private TvaRepository tvaRepository;
    @Autowired private VenteBatchService venteBatchService;

    private static class VenteKey {
        private final String codeArticle, codeCli, codeDate, codePromo;

        public VenteKey(String codeArticle, String codeCli, String codeDate,
                        String codePromo) {
            this.codeArticle = codeArticle;
            this.codeCli = codeCli;
            this.codeDate = codeDate;
            this.codePromo = codePromo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VenteKey)) return false;
            VenteKey that = (VenteKey) o;
            return Objects.equals(codeArticle, that.codeArticle) &&
                   Objects.equals(codeCli, that.codeCli) &&
                   Objects.equals(codeDate, that.codeDate) &&
                   Objects.equals(codePromo, that.codePromo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(codeArticle, codeCli, codeDate, codePromo);
        }
    }


    @Override
    public int getPriorite() {
        return 2;
    }


	@Override
	public boolean supports(String filename) {
		return filename.toLowerCase().contains("base");
	}


	@Override
	public void importData(Sheet sheet) throws IOException {
		System.out.println("--- Début de l'importation de Comparaison par lot ---");
		// --- Phase 1: Pré-chargement des données de référence en mémoire ---
		Map<String,Article> articlesExistants= articleRepository.findAll().stream()
				.collect(Collectors.toMap(Article::getCodeArticle, Function.identity()));
		Map<String,DatePerso> datesExistants= dateRepository.findAll().stream()
				.collect(Collectors.toMap(DatePerso::getCodeDate, Function.identity()));
		Map<String,Client> clientsExistants= clientRepository.findAll().stream()
				.collect(Collectors.toMap(Client:: getCodeCli, Function.identity()));
		Map<String,Labo> labosExistants= laboRepository.findAll().stream()
				.collect(Collectors.toMap(Labo:: getCodeLabo, Function.identity()));
		Map<String,Promotion> promosExistants= promoRepository.findAll().stream()
				.collect(Collectors.toMap(Promotion:: getCodePromo, Function.identity()));
		Map<String,Tva> tvasExistants= tvaRepository.findAll().stream()
				.collect(Collectors.toMap(Tva::getCodeTva, Function.identity()));
		Map<VenteKey, Vente> ventesExistantes = venteRepository.findAll().stream()
			    .collect(Collectors.toMap(
			        c -> new VenteKey(
			            c.getArticle().getCodeArticle(),
			            c.getClient().getCodeCli(),
			            c.getDate().getCodeDate(),
			            c.getPromotion() != null ? c.getPromotion().getCodePromo() : null
			        ),
			        Function.identity(),
			        (existing, replacement) -> existing // en cas de doublon, garder l’existant
			    ));

		System.out.println("Pré-chargement terminé.");
	
		 // --- Phase 2: Collecte des entités à créer ou à mettre à jour ---
        
        // Listes finales pour la sauvegarde par lots
		List<Article> articlesAMettreAJourOuCreer = new ArrayList<>();
		List<DatePerso> datesAMettreAJourOuCreer = new ArrayList<>();
		List<Client> clientsAMettreAJourOuCreer = new ArrayList<>();
		List<Labo> labosAMettreAJourOuCreer = new ArrayList<>();
		List<Promotion> promosAMettreAJourOuCreer = new ArrayList<>();
		List<Tva> tvasAMettreAJourOuCreer = new ArrayList<>();
		List<Vente> ventesAMettreAJourOuCreer = new ArrayList<>();
		 // Caches temporaires pour les nouvelles entités pour éviter les doublons dans le même fichier
        Map<String, Article> nouveauxArticlesCache = new HashMap<>();
        Map<String, DatePerso> nouveauxDatesCache = new HashMap<>();
        Map<String, Client> nouveauxClientsCache = new HashMap<>();
        Map<String, Labo> nouveauxLabosCache = new HashMap<>();
        Map<String, Promotion> nouveauxPromoCache = new HashMap<>();
        Map<String, Tva> nouveauxTvasCache = new HashMap<>();
        
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
            String codeClient = getStringValue(row.getCell(columnIndexMap.get("NOCLI")));
            String nomClient = getStringValue(row.getCell(columnIndexMap.get("NOMCLI")));
            String codeArticle = getStringValue(row.getCell(columnIndexMap.get("NOART")));
            String codeLabo = getStringValue(row.getCell(columnIndexMap.get("LABO")));
            String nomLabo = getStringValue(row.getCell(columnIndexMap.get("NOMLAB")));
            String codeTva = getStringValue(row.getCell(columnIndexMap.get("CODTVA"))).trim();
            String codePromo = getStringValue(row.getCell(columnIndexMap.get("NOPRM")));
            String nomPromo = getStringValue(row.getCell(columnIndexMap.get("NOMPRM")));
            String typePromo = getStringValue(row.getCell(columnIndexMap.get("TYPPRM")));
            int ug = (int) getNumericValue(row.getCell(columnIndexMap.get("UGLIV")));
            String codeVente = getStringValue(row.getCell(columnIndexMap.get("NOFACL")));
            int quantiteVendu = (int) getNumericValue(row.getCell(columnIndexMap.get("QTLVCL")));
            double montantVente = getNumericValue(row.getCell(columnIndexMap.get("MTLIG")));
            int jour = (int) getNumericValue(row.getCell(columnIndexMap.get("JJFACL")));
            int mois = (int) getNumericValue(row.getCell(columnIndexMap.get("MMFACL")));
            int annee = (int) getNumericValue(row.getCell(columnIndexMap.get("AAFACL")));
           
            //Gestion des Date
            LocalDate localDate;
            try {
                localDate = LocalDate.of(annee, mois, jour);
            } catch (DateTimeException e) {
                System.err.println("Date invalide à la ligne " + (rowIndex + 1) + ": " + e.getMessage());
                rowIndex++;
                continue;
            }
            String codeDate = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            DatePerso date= datesExistants.get(codeDate);
            if(date == null) {
            	date= nouveauxDatesCache.get(codeDate);
            	if(date == null) {
            		date= new DatePerso();
            		date.setAnnee(annee);
            		date.setCodeDate(codeDate);
            		date.setDate(java.sql.Date.valueOf(localDate));
            		date.setJour(jour);
            		date.setMois(mois);
            		nouveauxDatesCache.put(codeDate, date);
            	}
            	datesAMettreAJourOuCreer.add(date);    
            }
            
          //Gestion des clients
            Client client = clientsExistants.get(codeClient);
            if(client == null) {
            	client = nouveauxClientsCache.get(codeClient);
            	if(client == null) {
            		client=new Client();
            		client.setCodeCli(codeClient);
            		client.setNomCli(nomClient);
            		nouveauxClientsCache.put(codeClient, client);
            	}
            }
            clientsAMettreAJourOuCreer.add(client);
            
          //Gestion des labos
            Labo labo= labosExistants.get(codeLabo);
            if(labo == null) {
            	labo = nouveauxLabosCache.get(codeLabo);
            	if(labo == null) {
            		labo= new Labo();
            		labo.setCodeLabo(codeLabo);
            		labo.setNomLabo(nomLabo);         		
            		nouveauxLabosCache.put(codeLabo,labo);
            	}
            }
            labosAMettreAJourOuCreer.add(labo);
            
          //Gestion des promotion
            Promotion promo = promosExistants.get(codePromo);
            if(promo == null) {
            	promo = nouveauxPromoCache.get(codePromo);
            	if(promo == null) {
            		promo= new Promotion();
            		promo.setCodePromo(codePromo);
            		promo.setNomPromo(nomPromo);
            		promo.setTypePromo(typePromo);
            		promo.setUgLivre(ug);        		        		
            		nouveauxPromoCache.put(codePromo,promo);
            	}
            }
            promosAMettreAJourOuCreer.add(promo);
            
            //Gestion des Tvas
            Tva tva= tvasExistants.get(codeTva);
            if(tva== null) {
            	tva= nouveauxTvasCache.get(codeTva);     	
            	if(tva == null) {
            		tva = new Tva();
            		tva.setCodeTva(codeTva);
            		if (codeTva.equals("7")) {
                        tva.setTaux(20.0);
                        tva.setNature("Complément alimentaire");
                    } else {
                        tva.setTaux(0.0);
                        tva.setNature("Non défini");
                    }
            		nouveauxTvasCache.put(codeTva,tva);
            	}
            }
            tvasAMettreAJourOuCreer.add(tva);
            
            //Gestion des articles
            Article article = articlesExistants.get(codeArticle);
            if (article == null) {
                throw new RuntimeException("Article inexistant dans la base : " + codeArticle 
                    + " (ligne " + (rowIndex + 1) + ")");
            }else {
            	article.setLabo(labo);
            	article.setTva(tva);
            	
            }
            // Ici pas de création => on suppose que l'article existe déjà en DB
            articlesAMettreAJourOuCreer.add(article);

            
            //Gestion des ventes
            VenteKey venteKey= new VenteKey(article.getCodeArticle(),client.getCodeCli(),date.getCodeDate(),promo.getCodePromo());
            Vente vente = ventesExistantes.get(venteKey);
            if (vente == null) {
                vente = new Vente();
                vente.setCodeVente(codeVente);
                vente.setArticle(article);
                vente.setClient(client);
                vente.setDate(date);
                vente.setPromotion(promo);
                vente.setQuantiteVendu(quantiteVendu);
                vente.setMontantVente(montantVente);
                ventesExistantes.put(venteKey, vente);
               
            } else {
                // Mise à jour
            	vente.setCodeVente(codeVente);
                vente.setQuantiteVendu(vente.getQuantiteVendu() + quantiteVendu);
                vente.setMontantVente(vente.getMontantVente() + montantVente);
                
            }
            ventesAMettreAJourOuCreer.add(vente);
            rowIndex++;
        }
     // --- Phase 3: Insertion finale par lots dans la base de données ---
     // Sauvegarde
        venteBatchService.insertInBatch(datesAMettreAJourOuCreer);
        venteBatchService.insertInBatch(clientsAMettreAJourOuCreer);
        venteBatchService.insertInBatch(labosAMettreAJourOuCreer);
        venteBatchService.insertInBatch(promosAMettreAJourOuCreer);
        venteBatchService.insertInBatch(tvasAMettreAJourOuCreer);
        venteBatchService.insertInBatch(articlesAMettreAJourOuCreer);
        venteBatchService.insertInBatch(ventesAMettreAJourOuCreer);
        System.out.println("--- Import terminé. " + ventesAMettreAJourOuCreer.size() + " ventes traitées. ---");

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
	
}
