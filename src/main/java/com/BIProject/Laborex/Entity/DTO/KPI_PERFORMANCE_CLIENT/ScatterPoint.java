package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ScatterPoint {
	private long frequenceAchat;   // axe Y
	@JsonSerialize(using = PercentageSerializer.class)
    private double pourcentageCA;  // axe X
	public ScatterPoint() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ScatterPoint(long frequenceAchat, double pourcentageCA) {
		super();
		this.frequenceAchat = frequenceAchat;
		this.pourcentageCA = pourcentageCA;
	}
	public long getFrequenceAchat() {
		return frequenceAchat;
	}
	public void setFrequenceAchat(long frequenceAchat) {
		this.frequenceAchat = frequenceAchat;
	}
	public double getPourcentageCA() {
		return pourcentageCA;
	}
	public void setPourcentageCA(double pourcentageCA) {
		this.pourcentageCA = pourcentageCA;
	}
    

}
