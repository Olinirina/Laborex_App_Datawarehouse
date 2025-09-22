package com.BIProject.Laborex.Controller.ALERTE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.DTO.ALERTE.AlerteDTO;
import com.BIProject.Laborex.Service.ALERTE.AnomalieVenteService;
import com.BIProject.Laborex.Service.ALERTE.ClientInactifService;
import com.BIProject.Laborex.Service.ALERTE.StockCritiqueService;

@RestController
@RequestMapping("/api/alertes")
@PreAuthorize("hasRole('TRANSIT')")
public class AlerteController {
	@Autowired public StockCritiqueService calculAlerte;
	@Autowired public ClientInactifService inactifService;
	@Autowired public AnomalieVenteService anomalieService;
	//========================STOCK CRITIQUE============================
    @GetMapping("/critiques")
    public ResponseEntity<List<AlerteDTO>> getStockCritique(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order    // asc ou desc
    		) {
        return ResponseEntity.ok(calculAlerte.detecterStocksCritiques(search,sortBy,order));
    }
  //========================CLIENTS INACTIFS============================
    @GetMapping("/inactifs")
    public ResponseEntity<List<AlerteDTO>> getClientsInactifs(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order    // asc ou desc
    		) {
        return ResponseEntity.ok(inactifService.detecterClientsInactifs(search, sortBy, order));
    }
  //========================ANOMALIES DES VENTES============================
    @GetMapping("/anomalies-ventes")
    public ResponseEntity<List<AlerteDTO>> getAnomaliesDesVentes(
    		@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order    // asc ou desc
    		) {
        return ResponseEntity.ok(anomalieService.detecterAnomaliesVente(search, sortBy, order));
    }

}
