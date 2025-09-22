package com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class CamembertClassification {
	private String classe;
	@JsonSerialize(using= PercentageSerializer.class)
	private double pourcentage;
	public CamembertClassification() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CamembertClassification(String classe, double pourcentage) {
		super();
		this.classe = classe;
		this.pourcentage = pourcentage;
	}
	public String getClasse() {
		return classe;
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
	public double getPourcentage() {
		return pourcentage;
	}
	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}
	

}
