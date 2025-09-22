package com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AbcDto {
	private String codeElement;
	private String nomElement;
	@JsonSerialize(using= DoubleSerializer.class)
	private double chiffreAffaires;
	@JsonSerialize(using= PercentageSerializer.class)
	private double pourcentageCa;
	private CategorieClassification categorie;
	public AbcDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AbcDto(String codeElement, String nomElement, double chiffreAffaires, double pourcentageCa,
			CategorieClassification categorie) {
		super();
		this.codeElement = codeElement;
		this.nomElement = nomElement;
		this.chiffreAffaires = chiffreAffaires;
		this.pourcentageCa = pourcentageCa;
		this.categorie = categorie;
	}
	public String getCodeElement() {
		return codeElement;
	}
	public void setCodeElement(String codeElement) {
		this.codeElement = codeElement;
	}
	public String getNomElement() {
		return nomElement;
	}
	public void setNomElement(String nomElement) {
		this.nomElement = nomElement;
	}
	public double getChiffreAffaires() {
		return chiffreAffaires;
	}
	public void setChiffreAffaires(double chiffreAffaires) {
		this.chiffreAffaires = chiffreAffaires;
	}
	public double getPourcentageCa() {
		return pourcentageCa;
	}
	public void setPourcentageCa(double pourcentageCa) {
		this.pourcentageCa = pourcentageCa;
	}
	public CategorieClassification getCategorie() {
		return categorie;
	}
	public void setCategorie(CategorieClassification categorie) {
		this.categorie = categorie;
	}
	

}
