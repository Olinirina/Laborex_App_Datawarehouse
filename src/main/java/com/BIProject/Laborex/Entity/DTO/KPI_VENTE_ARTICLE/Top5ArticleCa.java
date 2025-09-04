package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_ARTICLE;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Top5ArticleCa {
	private String libArticle;
	@JsonSerialize(using = PercentageSerializer.class)
    private double pourcentage;
	public Top5ArticleCa() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Top5ArticleCa(String libArticle, double pourcentage) {
		super();
		this.libArticle = libArticle;
		this.pourcentage = pourcentage;
	}
	public String getLibArticle() {
		return libArticle;
	}
	public void setLibArticle(String libArticle) {
		this.libArticle = libArticle;
	}
	public double getPourcentage() {
		return pourcentage;
	}
	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}

    

}
