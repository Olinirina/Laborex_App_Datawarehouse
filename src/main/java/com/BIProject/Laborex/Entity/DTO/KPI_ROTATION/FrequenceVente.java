package com.BIProject.Laborex.Entity.DTO.KPI_ROTATION;



public class FrequenceVente {
	private String codeArticle;
	private String libelleArticle;
	private long nombreDeVentes;
	private CategorieRotation categorie;
	public FrequenceVente() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FrequenceVente(String codeArticle,String libelleArticle, long nombreDeVentes, CategorieRotation categorie) {
		super();
		this.codeArticle= codeArticle;
		this.libelleArticle = libelleArticle;
		this.nombreDeVentes = nombreDeVentes;
		this.categorie = categorie;
	}
	public String getCodeArticle() {
		return codeArticle;
	}
	public void setCodeArticle(String codeArticle) {
		this.codeArticle = codeArticle;
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
	public CategorieRotation getCategorie() {
		return categorie;
	}
	public void setCategorie(CategorieRotation categorie) {
		this.categorie = categorie;
	}
	

}
