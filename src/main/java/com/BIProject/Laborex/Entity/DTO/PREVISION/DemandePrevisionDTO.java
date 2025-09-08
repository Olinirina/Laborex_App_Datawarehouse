package com.BIProject.Laborex.Entity.DTO.PREVISION;

public class DemandePrevisionDTO {
	private String codeArticle;
    private String libelle;
    private int mois;            // Mois prévisionnel (1 = Jan, 2 = Fev, 3 = Mar)
    private double quantitePrevue;
	public DemandePrevisionDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DemandePrevisionDTO(String codeArticle, String libelle, int mois, double quantitePrevue) {
		super();
		this.codeArticle = codeArticle;
		this.libelle = libelle;
		this.mois = mois;
		this.quantitePrevue = quantitePrevue;
	}
	public String getCodeArticle() {
		return codeArticle;
	}
	public void setCodeArticle(String codeArticle) {
		this.codeArticle = codeArticle;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public int getMois() {
		return mois;
	}
	public void setMois(int mois) {
		this.mois = mois;
	}
	public double getQuantitePrevue() {
		return quantitePrevue;
	}
	public void setQuantitePrevue(double quantitePrevue) {
		this.quantitePrevue = quantitePrevue;
	} 
    

    
}

