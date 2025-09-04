package com.BIProject.Laborex.Controller.SEGMENTATION;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC.ClassificationABCDTO;
import com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM.SegmentationRFMDTO;
import com.BIProject.Laborex.Service.SEGMENTATION.SegmentationService;

@RestController
@RequestMapping("/api/segmentation")
public class SegmentationController {
	@Autowired public SegmentationService segmentationService;
	// ==================== CLASSIFICATION ABC /ARTICLES ====================
	@GetMapping("/abc/articles")
    public ResponseEntity<ClassificationABCDTO> getAbcArticles(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "ca") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order// asc ou desc 
	        ){
        return ResponseEntity.ok(segmentationService.getAbcAnalyseParArticle(search,sortBy,order));
    }
	// ==================== CLASSIFICATION ABC /CLIENTS ====================
	@GetMapping("/abc/clients")
    public ResponseEntity<ClassificationABCDTO> getAbcClients(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "ca") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order// asc ou desc 
	        ){
        return ResponseEntity.ok(segmentationService.getAbcAnalyseParClients(search,sortBy,order));
    }
	// ==================== CLASSIFICATION ABC /ARTICLES ====================
	@GetMapping("/abc/labos")
    public ResponseEntity<ClassificationABCDTO> getAbcLabos(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "ca") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order// asc ou desc 
	        ){
        return ResponseEntity.ok(segmentationService.getAbcAnalyseParLabos(search,sortBy,order));
    }
	// ==================== SEGMENTATION RFM CLIENTS ====================
	@GetMapping("/rfm")
    public ResponseEntity<SegmentationRFMDTO> getRFMSegmentation(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order// asc ou desc 
	        ){
        return ResponseEntity.ok(segmentationService.getRFMSegmentation(search,sortBy,order));
    }

}
