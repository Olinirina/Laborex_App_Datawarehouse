package com.BIProject.Laborex.Entity.DTO.TABLEAU_BORD;

import org.netlib.util.doubleW;

import com.BIProject.Laborex.UTIL.DoubleSmallSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class GraphiqueTableauBord {
	@JsonSerialize(using = DoubleSmallSerializer.class)
	private double nombreVente;
	private String mois;
	public GraphiqueTableauBord() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GraphiqueTableauBord(double nombreVente, String mois) {
		super();
		this.nombreVente = nombreVente;
		this.mois = mois;
	}
	public double getNombreVente() {
		return nombreVente;
	}
	public void setNombreVente(double nombreVente) {
		this.nombreVente = nombreVente;
	}
	public String getMois() {
		return mois;
	}
	public void setMois(String mois) {
		this.mois = mois;
	}
	

}
