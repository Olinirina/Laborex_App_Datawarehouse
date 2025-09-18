package com.BIProject.Laborex.Controller.KPI;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT.KpiPerformanceClient;
import com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO.KpiPerformanceLabo;
import com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION.KpiPromotion;
import com.BIProject.Laborex.Entity.DTO.KPI_ROTATION.KpiRotation;
import com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE.SaisonnaliteDTO;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_ARTICLE.KpiCaArticle;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT.KpiCaClient;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_LABO.KpiCaLabo;
import com.BIProject.Laborex.Service.KPI.KpiService;


@RestController
@RequestMapping("/api/kpi")
@PreAuthorize("hasAnyRole('COMMERCIAL','DG','TRANSIT')")
public class KpiController {
	@Autowired private KpiService kpiService;
	
	//=========CA PAR ARTICLE===========
	@GetMapping("/ca-par-article")
	public ResponseEntity<KpiCaArticle> getCaParArticle(
	        @RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "ca") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order    // asc ou desc
	) {
	    return ResponseEntity.ok(kpiService.getCaParArticle(search, sortBy, order));
	}
	//=========CA PAR CLIENT===========
		@GetMapping("/ca-par-client")
		public ResponseEntity<KpiCaClient> getCaParClient(
		        @RequestParam(required = false) String search,       // recherche texte
		        @RequestParam(defaultValue = "ca") String sortBy,    // codeCli, nomCli, ca
		        @RequestParam(defaultValue = "desc") String order    // asc ou desc
		) {
		    return ResponseEntity.ok(kpiService.getCaParClient(search, sortBy, order));
		}
		
		//=========CA PAR LABO===========
				@GetMapping("/ca-par-labo")
				public ResponseEntity<KpiCaLabo> getCaParLabo(
				        @RequestParam(required = false) String search,       // recherche texte
				        @RequestParam(defaultValue = "ca") String sortBy,    // codeCli, nomCli, ca
				        @RequestParam(defaultValue = "desc") String order    // asc ou desc
				) {
				    return ResponseEntity.ok(kpiService.getCaParLabo(search, sortBy, order));
				}

		//=========ROTATION===========		
		@GetMapping("/rotation")
		public ResponseEntity<KpiRotation> getRotation(
			@RequestParam(required = false) String search,  
			@RequestParam(defaultValue = "nbVentes") String sortBy,    //codeArticle,libArticle,nbVentes
			@RequestParam(defaultValue = "desc") String order    
			) {
				return ResponseEntity.ok(kpiService.getKpiRotation(search, sortBy, order));
		}
		//=========PERFORMANCE CLIENTS===========		
				@GetMapping("/performance-clients")
				public ResponseEntity<KpiPerformanceClient> getPerformanceClient(
					@RequestParam(required = false) String search,  
					@RequestParam(defaultValue = "ChiffreAffaires") String sortBy,    //
					@RequestParam(defaultValue = "desc") String order    
					) {
						return ResponseEntity.ok(kpiService.getKpiPerformanceClients(search, sortBy, order));
				}
				//=========PERFORMANCE LABO===========		
				@GetMapping("/performance-labos")
				public ResponseEntity<KpiPerformanceLabo> getPerformanceLabos(
					@RequestParam(required = false) String search,  
					@RequestParam(defaultValue = "ChiffreAffaires") String sortBy,    //
					@RequestParam(defaultValue = "desc") String order    
					) {
						return ResponseEntity.ok(kpiService.getKpiPerformanceLabo(search, sortBy, order));
				}
				
				//=========SAISONNALITE DES VENTES===========		
				@GetMapping("/saisonnalite")
				public ResponseEntity<SaisonnaliteDTO> getSaisonnaliteVente(
					@RequestParam(required = false) String search,  
					@RequestParam(defaultValue = "ChiffreAffaires") String sortBy,    //
					@RequestParam(defaultValue = "desc") String order    
					) {
						return ResponseEntity.ok(kpiService.getSaisonnalite(search, sortBy, order));
				}
				
				//=========IMPACT PROMOTION SUR LES VENTES ===========		
				@GetMapping("/impact-promo")
				public ResponseEntity<KpiPromotion> getImpactPromo(
						@RequestParam(required = false) String search,  
						@RequestParam(defaultValue = "ChiffreAffaires") String sortBy,    //
						@RequestParam(defaultValue = "desc") String order    ) {
						return ResponseEntity.ok(kpiService.getImpactPromo(search, sortBy, order));
				}

}
