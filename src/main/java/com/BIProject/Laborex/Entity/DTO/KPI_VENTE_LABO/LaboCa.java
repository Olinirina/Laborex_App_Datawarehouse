package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_LABO;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class LaboCa {
	private String codeLabo;
	private String nomLabo;
	@JsonSerialize(using = DoubleSerializer.class)
	private double ca;
	 @JsonSerialize(using = PercentageSerializer.class)
	private double pourcentage;
	 public LaboCa() {
		super();
		// TODO Auto-generated constructor stub
	 }
	 public LaboCa(String codeLabo, String nomLabo, double ca, double pourcentage) {
		super();
		this.codeLabo = codeLabo;
		this.nomLabo = nomLabo;
		this.ca = ca;
		this.pourcentage = pourcentage;
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
