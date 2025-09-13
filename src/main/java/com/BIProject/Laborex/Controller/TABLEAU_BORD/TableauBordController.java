package com.BIProject.Laborex.Controller.TABLEAU_BORD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BIProject.Laborex.Entity.DTO.TABLEAU_BORD.TableauBordDTO;
import com.BIProject.Laborex.Service.TABLEAU_BORD.TableauBordService;



@RestController
@RequestMapping("/api")
public class TableauBordController {
	@Autowired public TableauBordService service;
	@GetMapping("/tableau-bord")
    public TableauBordDTO tableauDeBord() throws Exception {
        return service.buildTableauBord();
    }

}
