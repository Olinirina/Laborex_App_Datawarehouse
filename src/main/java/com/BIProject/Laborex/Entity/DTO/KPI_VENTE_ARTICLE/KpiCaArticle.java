package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_ARTICLE;

import java.util.List;

import com.BIProject.Laborex.UTIL.DoubleSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class KpiCaArticle {
	@JsonSerialize(using = DoubleSerializer.class)
	private double caGlobal;
    private List<ArticleCa> articles; // Tous les articles avec CA + %
    private List<Top5ArticleCa> top5; // Les 5 premiers
	public KpiCaArticle() {
		super();
		// TODO Auto-generated constructor stub
	}
	public KpiCaArticle(double caGlobal, List<ArticleCa> articles, List<Top5ArticleCa> top5) {
		super();
		this.caGlobal = caGlobal;
		this.articles = articles;
		this.top5 = top5;
	}
	public double getCaGlobal() {
		return caGlobal;
	}
	public void setCaGlobal(double caGlobal) {
		this.caGlobal = caGlobal;
	}
	public List<ArticleCa> getArticles() {
		return articles;
	}
	public void setArticles(List<ArticleCa> articles) {
		this.articles = articles;
	}
	public List<Top5ArticleCa> getTop5() {
		return top5;
	}
	public void setTop5(List<Top5ArticleCa> top5) {
		this.top5 = top5;
	}
    

}
