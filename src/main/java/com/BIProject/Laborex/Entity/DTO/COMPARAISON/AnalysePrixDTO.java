package com.BIProject.Laborex.Entity.DTO.COMPARAISON;

import java.util.List;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AnalysePrixDTO {
	@JsonSerialize(using= PercentageSerializer.class)
	private double margeBasse;
	@JsonSerialize(using= PercentageSerializer.class)
	private double pourcentagePrix;
	private int classementMoyen;
	private List<ComparaisonDTO> compar;
	public AnalysePrixDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AnalysePrixDTO(double margeBasse, double pourcentagePrix, int classementMoyen, List<ComparaisonDTO> compar) {
		super();
		this.margeBasse = margeBasse;
		this.pourcentagePrix = pourcentagePrix;
		this.classementMoyen = classementMoyen;
		this.compar = compar;
	}
	public double getMargeBasse() {
		return margeBasse;
	}
	public void setMargeBasse(double margeBasse) {
		this.margeBasse = margeBasse;
	}
	public double getPourcentagePrix() {
		return pourcentagePrix;
	}
	public void setPourcentagePrix(double pourcentagePrix) {
		this.pourcentagePrix = pourcentagePrix;
	}
	public int getClassementMoyen() {
		return classementMoyen;
	}
	public void setClassementMoyen(int classementMoyen) {
		this.classementMoyen = classementMoyen;
	}
	public List<ComparaisonDTO> getCompar() {
		return compar;
	}
	public void setCompar(List<ComparaisonDTO> compar) {
		this.compar = compar;
	}
	
	

}
