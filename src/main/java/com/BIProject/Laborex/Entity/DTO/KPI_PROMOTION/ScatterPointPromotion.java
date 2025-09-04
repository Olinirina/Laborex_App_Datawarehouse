package com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ScatterPointPromotion {
	private int ug; // axe Y
	@JsonSerialize(using = PercentageSerializer.class)
    private double pourcentageCA;  // axe X
	public ScatterPointPromotion() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ScatterPointPromotion(int ug, double pourcentageCA) {
		super();
		this.ug = ug;
		this.pourcentageCA = pourcentageCA;
	}
	public int getUg() {
		return ug;
	}
	public void setUg(int ug) {
		this.ug = ug;
	}
	public double getPourcentageCA() {
		return pourcentageCA;
	}
	public void setPourcentageCA(double pourcentageCA) {
		this.pourcentageCA = pourcentageCA;
	}
	

}
