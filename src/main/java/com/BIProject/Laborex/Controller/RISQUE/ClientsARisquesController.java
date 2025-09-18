package com.BIProject.Laborex.Controller.RISQUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.DTO.RISQUE.RisqueDTO;
import com.BIProject.Laborex.Service.RISQUE.ClientsRisqueService;

@RestController
@RequestMapping("/api/prevision")
@PreAuthorize("hasAnyRole('DG','TRANSIT','COMMERCIAL')")
public class ClientsARisquesController {
	@Autowired public ClientsRisqueService service;
	@GetMapping("/clientsRisques")
    public RisqueDTO score(
            @RequestParam(name = "window", defaultValue = "6") int window,
            @RequestParam(name = "horizon", defaultValue = "2") int horizon,
            @RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order
    ) throws Exception {
        return service.scoreClientsWeka(window, horizon,search,sortBy,order);
    }

}
