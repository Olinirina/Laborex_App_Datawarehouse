package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PerformanceLaboDTO {
	private String codeLabo;
	private String nomLabo;
	@JsonSerialize(using = DoubleSerializer.class)
    private double chiffreAffaires;
    private long nombreArticles;
    private TypeLabo categorie;
	public PerformanceLaboDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PerformanceLaboDTO(String codeLabo, String nomLabo, double chiffreAffaires, long nombreArticles,
			TypeLabo categorie) {
		super();
		this.codeLabo = codeLabo;
		this.nomLabo = nomLabo;
		this.chiffreAffaires = chiffreAffaires;
		this.nombreArticles = nombreArticles;
		this.categorie = categorie;
	}
	public String getCodeLabo() {
		return codeLabo;
	}
	public void setCodeLabo(String codeLabo) {
		this.codeLabo = codeLabo;
	}
	public String getNomLabo() {
		return nomLabo;
	}
	public void setNomLabo(String nomLabo) {
		this.nomLabo = nomLabo;
	}
	public double getChiffreAffaires() {
		return chiffreAffaires;
	}
	public void setChiffreAffaires(double chiffreAffaires) {
		this.chiffreAffaires = chiffreAffaires;
	}
	public long getNombreArticles() {
		return nombreArticles;
	}
	public void setNombreArticles(long nombreArticles) {
		this.nombreArticles = nombreArticles;
	}
	public TypeLabo getCategorie() {
		return categorie;
	}
	public void setCategorie(TypeLabo categorie) {
		this.categorie = categorie;
	}

}
