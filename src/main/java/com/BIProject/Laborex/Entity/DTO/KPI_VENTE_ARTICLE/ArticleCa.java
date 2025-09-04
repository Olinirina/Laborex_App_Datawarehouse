package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_ARTICLE;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ArticleCa {
	private String codeArticle;
    private String libArticle;
    @JsonSerialize(using = DoubleSerializer.class)
    private double ca;
    @JsonSerialize(using = PercentageSerializer.class)
    private double pourcentage;
	public ArticleCa() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ArticleCa(String codeArticle, String libArticle, double ca, double pourcentage) {
		super();
		this.codeArticle = codeArticle;
		this.libArticle = libArticle;
		this.ca = ca;
		this.pourcentage = pourcentage;
	}
	public String getCodeArticle() {
		return codeArticle;
	}
	public void setCodeArticle(String codeArticle) {
		this.codeArticle = codeArticle;
	}
	public String getLibArticle() {
		return libArticle;
	}
	public void setLibArticle(String libArticle) {
		this.libArticle = libArticle;
	}
	public double getCa() {
		return ca;
	}
	public void setCa(double ca) {
		this.ca = ca;
	}
	public double getPourcentage() {
		return pourcentage;
	}
	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}

}
