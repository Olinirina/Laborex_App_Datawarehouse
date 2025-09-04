package com.BIProject.Laborex.Service.IMPORTATION;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.BIProject.Laborex.Entity.IMPORTATION.ImportResult;
import com.monitorjbl.xlsx.StreamingReader;
//Gérer l’importation de plusieurs fichiers Excel différents en appelant le bon importer spécialisé.
@Service
public class MultiSourceExcelImportService {

	@Autowired
    public List<ExcelImportation> importers;
	@Autowired
    private DuckDBSyncService duckDBSyncService;

    public List<ImportResult> importerFichiers(List<MultipartFile> fichiers) {
        // Associer chaque fichier au bon importer (via supports)
        List<ImporterFichierPair> fichiersAvecImporters = new ArrayList<>();
        List<ImportResult> results = new ArrayList<>();

        for (MultipartFile file : fichiers) {
            String fileName = file.getOriginalFilename();

            // Valider extension tôt (et produire un résultat d'échec si mauvais)
            if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
                ImportResult bad = baseResult(file);
                bad.setStatus("FAILED");
                bad.setMessage("Seuls les fichiers .xls ou .xlsx sont autorisés");
                results.add(bad);
                continue;
            }

            ExcelImportation importer = importers.stream()
                    .filter(i -> i.supports(fileName))
                    .findFirst()
                    .orElse(null);

            if (importer == null) {
                ImportResult bad = baseResult(file);
                bad.setStatus("FAILED");
                bad.setMessage("Aucun importer ne supporte le fichier : " + fileName);
                results.add(bad);
                continue;
            }

            fichiersAvecImporters.add(new ImporterFichierPair(file, importer));
        }

        // Trier par priorité ASC (1 avant 2 avant 3, etc.)
        fichiersAvecImporters.sort(Comparator.comparingInt(pair -> pair.importer.getPriorite()));

        // Traiter chaque fichier et produire un ImportResult (succès/échec)
        for (ImporterFichierPair pair : fichiersAvecImporters) {
            MultipartFile file = pair.file;
            ExcelImportation importer = pair.importer;

            ImportResult res = baseResult(file);
            res.setImporter(importer.getClass().getSimpleName());
            res.setPriority(importer.getPriorite());
            res.setStartedAt(Instant.now());

            Workbook workbook = null;
            try {
                workbook = StreamingReader.builder()
                        .rowCacheSize(100)
                        .bufferSize(4096)
                        .open(file.getInputStream());

                Sheet sheet;
                // Cas particulier : Comparaison → feuille "Feuil2"
                if (importer instanceof ComparaisonExcelImporter) {
                    sheet = workbook.getSheet("Feuil2");
                    res.setSheet("Feuil2");
                    if (sheet == null) {
                        throw new IllegalArgumentException("La feuille 'Feuil2' est introuvable dans " + res.getFileName());
                    }
                } else {
                    sheet = workbook.getSheetAt(0);
                    res.setSheet("index 0");
                }

                importer.importData(sheet);
                res.setStatus("SUCCESS");
                res.setMessage("Importation terminée avec succès");

            } catch (Exception e) {
                res.setStatus("FAILED");
                res.setMessage("Erreur : " + e.getMessage());
            } finally {
                try { if (workbook != null) workbook.close(); } catch (IOException ignore) {}
                res.setFinishedAt(Instant.now());
                res.setDurationMs(Duration.between(res.getStartedAt(), res.getFinishedAt()).toMillis());
                results.add(res);
            }
            duckDBSyncService.synchroniserToutesLesDonnees();
        }
        
        return results;
    }

    // Fabrique un ImportResult de base
    private ImportResult baseResult(MultipartFile file) {
        ImportResult res = new ImportResult();
        res.setFileName(file.getOriginalFilename());
        res.setFileSize(file.getSize());
        return res;
    }

    // Petit pair fichier/importer pour trier puis traiter
    private static class ImporterFichierPair {
        MultipartFile file;
        ExcelImportation importer;

        ImporterFichierPair(MultipartFile file, ExcelImportation importer) {
            this.file = file;
            this.importer = importer;
        }
    }
}
