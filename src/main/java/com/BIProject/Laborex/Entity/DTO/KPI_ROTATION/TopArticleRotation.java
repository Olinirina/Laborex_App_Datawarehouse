package com.BIProject.Laborex.Entity.DTO.KPI_ROTATION;

public class TopArticleRotation {
	private String libelleArticle;
	private long nombreDeVentes;
	public TopArticleRotation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TopArticleRotation(String libelleArticle, long nombreDeVentes) {
		super();
		this.libelleArticle = libelleArticle;
		this.nombreDeVentes = nombreDeVentes;
	}
	public String getLibelleArticle() {
		return libelleArticle;
	}
	public void setLibelleArticle(String libelleArticle) {
		this.libelleArticle = libelleArticle;
	}
	public long getNombreDeVentes() {
		return nombreDeVentes;
	}
	public void setNombreDeVentes(long nombreDeVentes) {
		this.nombreDeVentes = nombreDeVentes;
	}
	

}
