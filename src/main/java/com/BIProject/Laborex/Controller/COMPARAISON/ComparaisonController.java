package com.BIProject.Laborex.Controller.COMPARAISON;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.DTO.COMPARAISON.AnalysePrixDTO;
import com.BIProject.Laborex.Entity.DTO.COMPARAISON.ComparaisonDTO;
import com.BIProject.Laborex.Entity.DTO.COMPARAISON.SimulationDTO;
import com.BIProject.Laborex.Service.COMPARAISON.AnalysePrixService;


@RestController
@RequestMapping("/api/comparaisons")
@PreAuthorize("hasRole('TRANSIT')")
public class ComparaisonController {
	@Autowired public AnalysePrixService priceAnalysisService;
	@GetMapping("/analyse-complete")
    public AnalysePrixDTO obtenirAnalyseComplete(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle
	        @RequestParam(defaultValue = "desc") String order    // asc ou desc
    		) {
           return priceAnalysisService.calculerAnalyseComplete(search,sortBy,order);
       
    }
	 /**
     * Endpoint pour l'analyse d'un article spécifique avec métriques avancées
     * GET /api/comparaisons/analyse/{codeArticle}
     */
    @GetMapping("/analyse/{codeArticle}")
    public ResponseEntity<List<ComparaisonDTO>> obtenirAnalysePourArticle(@PathVariable String codeArticle,
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle
	        @RequestParam(defaultValue = "desc") String order    // asc ou desc
    		) {
        try {
            List<ComparaisonDTO> analyse = priceAnalysisService.calculerAnalysePourArticle(codeArticle,search,sortBy,order);
            
            if (analyse.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(analyse);
        } catch (Exception e) {
            System.err.println("Erreur dans obtenirAnalysePourArticle: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/simulation")
    public List<SimulationDTO> obtenirAnalyseComplete(
    		) {
           return priceAnalysisService.simulerTousLesArticles();
       
    }

}
