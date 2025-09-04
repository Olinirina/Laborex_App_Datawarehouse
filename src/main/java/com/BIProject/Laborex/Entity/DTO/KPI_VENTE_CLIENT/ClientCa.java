package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClientCa {
	private String codeClient;
	private String nomClient;
	 @JsonSerialize(using = DoubleSerializer.class)
	private double ca;
	 @JsonSerialize(using = PercentageSerializer.class)
	private double pourcentage;
	 public ClientCa() {
		super();
		// TODO Auto-generated constructor stub
	 }
	 public ClientCa(String codeClient, String nomClient, double ca, double pourcentage) {
		super();
		this.codeClient = codeClient;
		this.nomClient = nomClient;
		this.ca = ca;
		this.pourcentage = pourcentage;
	 }
	 public String getCodeClient() {
		 return codeClient;
	 }
	 public void setCodeClient(String codeClient) {
		 this.codeClient = codeClient;
	 }
	 public String getNomClient() {
		 return nomClient;
	 }
	 public void setNomClient(String nomClient) {
		 this.nomClient = nomClient;
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
