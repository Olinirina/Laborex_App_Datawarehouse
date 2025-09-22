package com.BIProject.Laborex.Entity.DTO.TABLEAU_BORD;

import java.util.List;

import com.BIProject.Laborex.Entity.DTO.ALERTE.TopAlertesDTO;
import com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT.Top5ClientCa;
import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.DoubleSmallSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TableauBordDTO {
	@JsonSerialize(using = DoubleSerializer.class)
	private double Caglobal;
	@JsonSerialize(using = DoubleSmallSerializer.class)
	private double articlesVendus;
	@JsonSerialize(using = DoubleSmallSerializer.class)
	private double clientActifs;
	@JsonSerialize(using = PercentageSerializer.class)
	private double margeMoyenne;
	private List<GraphiqueTableauBord> graphiques;
	private List<Top5ClientCa> topClients;
	private List<TopAlertesDTO> alertes;
	public TableauBordDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TableauBordDTO(double caglobal, double articlesVendus, double clientActifs, double margeMoyenne,
			List<GraphiqueTableauBord> graphiques, List<Top5ClientCa> topClients, List<TopAlertesDTO> alertes) {
		super();
		Caglobal = caglobal;
		this.articlesVendus = articlesVendus;
		this.clientActifs = clientActifs;
		this.margeMoyenne = margeMoyenne;
		this.graphiques = graphiques;
		this.topClients = topClients;
		this.alertes = alertes;
	}
	public double getCaglobal() {
		return Caglobal;
	}
	public void setCaglobal(double caglobal) {
		Caglobal = caglobal;
	}
	public double getArticlesVendus() {
		return articlesVendus;
	}
	public void setArticlesVendus(double articlesVendus) {
		this.articlesVendus = articlesVendus;
	}
	public double getClientActifs() {
		return clientActifs;
	}
	public void setClientActifs(double clientActifs) {
		this.clientActifs = clientActifs;
	}
	public double getMargeMoyenne() {
		return margeMoyenne;
	}
	public void setMargeMoyenne(double margeMoyenne) {
		this.margeMoyenne = margeMoyenne;
	}
	public List<GraphiqueTableauBord> getGraphiques() {
		return graphiques;
	}
	public void setGraphiques(List<GraphiqueTableauBord> graphiques) {
		this.graphiques = graphiques;
	}
	public List<Top5ClientCa> getTopClients() {
		return topClients;
	}
	public void setTopClients(List<Top5ClientCa> topClients) {
		this.topClients = topClients;
	}
	public List<TopAlertesDTO> getAlertes() {
		return alertes;
	}
	public void setAlertes(List<TopAlertesDTO> alertes) {
		this.alertes = alertes;
	}
	

}
