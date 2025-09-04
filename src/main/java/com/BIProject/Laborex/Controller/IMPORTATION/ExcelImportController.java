package com.BIProject.Laborex.Controller.IMPORTATION;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.BIProject.Laborex.Entity.IMPORTATION.ImportResult;
import com.BIProject.Laborex.Service.IMPORTATION.MultiSourceExcelImportService;
@RestController
@RequestMapping("/api/import-excel")
public class ExcelImportController {

	 @Autowired
	    private MultiSourceExcelImportService importService;

	    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	    public ResponseEntity<List<ImportResult>> importerFichiers(@RequestParam("fichiers") List<MultipartFile> fichiers) {
	        // On délègue la validation d’extension + routage + import + status au service
	        List<ImportResult> results = importService.importerFichiers(fichiers);

	        // Si au moins un FAILED → 207 Multi-Status (facultatif),
	        // sinon 200 OK.
	        boolean allOk = results.stream().allMatch(r -> "SUCCESS".equals(r.getStatus()));
	        HttpStatus status = allOk ? HttpStatus.OK : HttpStatus.MULTI_STATUS;

	        return ResponseEntity.status(status).body(results);
	    }
}

