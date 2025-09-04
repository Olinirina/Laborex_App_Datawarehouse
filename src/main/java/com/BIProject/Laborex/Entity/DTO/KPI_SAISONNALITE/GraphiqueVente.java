package com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE;


import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class GraphiqueVente {
	private String mois;
	@JsonSerialize(using = PercentageSerializer.class)
	private double pourcentage;
	public GraphiqueVente() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GraphiqueVente(String mois, double pourcentage) {
		super();
		this.mois = mois;
		this.pourcentage = pourcentage;
	}
	public String getMois() {
		return mois;
	}
	public void setMois(String mois) {
		this.mois = mois;
	}
	public double getPourcentage() {
		return pourcentage;
	}
	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}
	

}
