package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ScatterPointLabo {
	private long nombreArticle;   // axe Y
	@JsonSerialize(using = PercentageSerializer.class)
    private double pourcentageCA;  // axe X
	public ScatterPointLabo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ScatterPointLabo(long nombreArticle, double pourcentageCA) {
		super();
		this.nombreArticle = nombreArticle;
		this.pourcentageCA = pourcentageCA;
	}
	public long getNombreArticle() {
		return nombreArticle;
	}
	public void setNombreArticle(long nombreArticle) {
		this.nombreArticle = nombreArticle;
	}
	public double getPourcentageCA() {
		return pourcentageCA;
	}
	public void setPourcentageCA(double pourcentageCA) {
		this.pourcentageCA = pourcentageCA;
	}
	

}
