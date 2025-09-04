package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_LABO;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Top5LaboCa {
	private String nomLabo;
	@JsonSerialize(using = PercentageSerializer.class)
	private double pourcentage;
	public Top5LaboCa() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Top5LaboCa(String nomLabo, double pourcentage) {
		super();
		this.nomLabo = nomLabo;
		this.pourcentage = pourcentage;
	}
	public String getNomLabo() {
		return nomLabo;
	}
	public void setNomLabo(String nomLabo) {
		this.nomLabo = nomLabo;
	}
	public double getPourcentage() {
		return pourcentage;
	}
	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}
	

}
