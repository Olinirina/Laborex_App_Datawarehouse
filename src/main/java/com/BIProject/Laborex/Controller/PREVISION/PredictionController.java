package com.BIProject.Laborex.Controller.PREVISION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.DTO.PREVISION.PrevisionDTO;
import com.BIProject.Laborex.Service.PREVISON.PrevisionService;

@RestController
@RequestMapping("/api/prediction")
public class PredictionController {

    @Autowired private PrevisionService predictionService;

    @GetMapping("/weka/3months")
    public PrevisionDTO getPrediction3Months(@RequestParam(required = false) String search,       // recherche texte
	        @RequestParam(defaultValue = "code") String sortBy,    // codeArticle, libArticle, ca
	        @RequestParam(defaultValue = "desc") String order// asc ou desc ) {
	        ) {
        return predictionService.predictNext3Months(search,sortBy,order);
    }
    
}
