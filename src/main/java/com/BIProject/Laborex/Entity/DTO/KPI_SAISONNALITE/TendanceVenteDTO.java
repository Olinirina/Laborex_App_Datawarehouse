package com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TendanceVenteDTO {
	private int annee;
	private int mois;
	@JsonSerialize(using= DoubleSerializer.class)
	private double chiffreAffaires;
	@JsonSerialize(using= PercentageSerializer.class)
	private double pourcentage;
	private TypeVente categorie;
	
	public TendanceVenteDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public double getChiffreAffaires() {
		return chiffreAffaires;
	}

	public void setChiffreAffaires(double chiffreAffaires) {
		this.chiffreAffaires = chiffreAffaires;
	}

	public double getPourcentage() {
		return pourcentage;
	}

	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}

	public TendanceVenteDTO(int annee, int mois, double chiffreAffaires, double pourcentage, TypeVente categorie) {
		super();
		this.annee = annee;
		this.mois = mois;
		this.chiffreAffaires = chiffreAffaires;
		this.pourcentage = pourcentage;
		this.categorie = categorie;
	}

	public int getAnnee() {
		return annee;
	}
	public void setAnnee(int annee) {
		this.annee = annee;
	}
	public int getMois() {
		return mois;
	}
	public void setMois(int mois) {
		this.mois = mois;
	}
	public TypeVente getCategorie() {
		return categorie;
	}
	public void setCategorie(TypeVente categorie) {
		this.categorie = categorie;
	}
	

}
