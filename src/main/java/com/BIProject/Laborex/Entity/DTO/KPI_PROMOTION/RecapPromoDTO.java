package com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class RecapPromoDTO {
	private String codePromo;
	private String nomPromo;
	private String typePromo;
	private int ug;
	@JsonSerialize(using = DoubleSerializer.class)
	private double chiffreAffaire;
	@JsonSerialize(using = PercentageSerializer.class)
    private double pourcentageCA; 
	private CategoriePromo categorie;
	public RecapPromoDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RecapPromoDTO(String codePromo, String nomPromo, String typePromo, int ug,
			double chiffreAffaire, double pourcentageCA, CategoriePromo categorie) {
		super();
		this.codePromo = codePromo;
		this.nomPromo = nomPromo;
		this.typePromo = typePromo;
		this.ug = ug;
		this.chiffreAffaire = chiffreAffaire;
		this.pourcentageCA= pourcentageCA;
		this.categorie = categorie;
	}
	public String getCodePromo() {
		return codePromo;
	}
	public void setCodePromo(String codePromo) {
		this.codePromo = codePromo;
	}
	public String getNomPromo() {
		return nomPromo;
	}
	public void setNomPromo(String nomPromo) {
		this.nomPromo = nomPromo;
	}
	public String getTypePromo() {
		return typePromo;
	}
	public double getPourcentageCA() {
		return pourcentageCA;
	}
	public void setPourcentageCA(double pourcentageCA) {
		this.pourcentageCA = pourcentageCA;
	}
	public void setTypePromo(String typePromo) {
		this.typePromo = typePromo;
	}
	public int getUg() {
		return ug;
	}
	public void setUg(int ug) {
		this.ug = ug;
	}
	public double getChiffreAffaire() {
		return chiffreAffaire;
	}
	public void setChiffreAffaire(double chiffreAffaire) {
		this.chiffreAffaire = chiffreAffaire;
	}
	public CategoriePromo getCategorie() {
		return categorie;
	}
	public void setCategorie(CategoriePromo categorie) {
		this.categorie = categorie;
	}
	
	

}
