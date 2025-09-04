package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Top5ClientCa {
	private String nomClient;
	@JsonSerialize(using = PercentageSerializer.class)
	private double pourcentage;
	public Top5ClientCa() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Top5ClientCa(String nomClient, double pourcentage) {
		super();
		this.nomClient = nomClient;
		this.pourcentage = pourcentage;
	}
	public String getNomClient() {
		return nomClient;
	}
	public void setNomClient(String nomClient) {
		this.nomClient = nomClient;
	}
	public double getPourcentage() {
		return pourcentage;
	}
	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}
	

}
