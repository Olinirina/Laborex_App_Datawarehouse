package com.BIProject.Laborex.Entity.DTO.COMPARAISON;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.DoubleSmallSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ComparaisonDTO {
	private String codeArticle;
    private String libelleArticle;
    @JsonSerialize(using=DoubleSmallSerializer.class)
    private Double prixVenteArticle;
    private String codeConcurrent;
    private String nomConcurrent;
    @JsonSerialize(using=DoubleSmallSerializer.class)
    private Double prixConcurrent;
    @JsonSerialize(using=DoubleSmallSerializer.class)
    private Double differenceAbsolue;
    @JsonSerialize(using=PercentageSerializer.class)
    private Double differencePourcentage;
    private StatutCompar statutPositionnement; // "Moins cher", "Égal", "Plus cher"
    private Integer classementConcurrentiel; // Rang de notre prix
    private Boolean alerteMargeBasse; // Alerte si marge < seuil
    private Boolean alertePrixEleve; // Alerte si prix > seuil marché
    @JsonSerialize(using=DoubleSmallSerializer.class)
    private Double minPrixConcurrent; // Prix concurrent minimum
    @JsonSerialize(using=DoubleSmallSerializer.class)
    private Double maxPrixConcurrent; // Prix concurrent maximum
    @JsonSerialize(using=PercentageSerializer.class)
    private Double margeActuelle; // Marge actuelle en %
	public ComparaisonDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ComparaisonDTO(String codeArticle, String libelleArticle, Double prixVenteArticle, String codeConcurrent,
			String nomConcurrent, Double prixConcurrent, Double differenceAbsolue, Double differencePourcentage,
			StatutCompar statutPositionnement, Integer classementConcurrentiel, Boolean alerteMargeBasse,
			Boolean alertePrixEleve, Double minPrixConcurrent, Double maxPrixConcurrent, Double margeActuelle) {
		super();
		this.codeArticle = codeArticle;
		this.libelleArticle = libelleArticle;
		this.prixVenteArticle = prixVenteArticle;
		this.codeConcurrent = codeConcurrent;
		this.nomConcurrent = nomConcurrent;
		this.prixConcurrent = prixConcurrent;
		this.differenceAbsolue = differenceAbsolue;
		this.differencePourcentage = differencePourcentage;
		this.statutPositionnement = statutPositionnement;
		this.classementConcurrentiel = classementConcurrentiel;
		this.alerteMargeBasse = alerteMargeBasse;
		this.alertePrixEleve = alertePrixEleve;
		this.minPrixConcurrent = minPrixConcurrent;
		this.maxPrixConcurrent = maxPrixConcurrent;
		this.margeActuelle = margeActuelle;
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
	public Double getPrixVenteArticle() {
		return prixVenteArticle;
	}
	public void setPrixVenteArticle(Double prixVenteArticle) {
		this.prixVenteArticle = prixVenteArticle;
	}
	public String getCodeConcurrent() {
		return codeConcurrent;
	}
	public void setCodeConcurrent(String codeConcurrent) {
		this.codeConcurrent = codeConcurrent;
	}
	public String getNomConcurrent() {
		return nomConcurrent;
	}
	public void setNomConcurrent(String nomConcurrent) {
		this.nomConcurrent = nomConcurrent;
	}
	public Double getPrixConcurrent() {
		return prixConcurrent;
	}
	public void setPrixConcurrent(Double prixConcurrent) {
		this.prixConcurrent = prixConcurrent;
	}
	public Double getDifferenceAbsolue() {
		return differenceAbsolue;
	}
	public void setDifferenceAbsolue(Double differenceAbsolue) {
		this.differenceAbsolue = differenceAbsolue;
	}
	public Double getDifferencePourcentage() {
		return differencePourcentage;
	}
	public void setDifferencePourcentage(Double differencePourcentage) {
		this.differencePourcentage = differencePourcentage;
	}
	public StatutCompar getStatutPositionnement() {
		return statutPositionnement;
	}
	public void setStatutPositionnement(StatutCompar statutPositionnement) {
		this.statutPositionnement = statutPositionnement;
	}
	public Integer getClassementConcurrentiel() {
		return classementConcurrentiel;
	}
	public void setClassementConcurrentiel(Integer classementConcurrentiel) {
		this.classementConcurrentiel = classementConcurrentiel;
	}
	public Boolean getAlerteMargeBasse() {
		return alerteMargeBasse;
	}
	public void setAlerteMargeBasse(Boolean alerteMargeBasse) {
		this.alerteMargeBasse = alerteMargeBasse;
	}
	public Boolean getAlertePrixEleve() {
		return alertePrixEleve;
	}
	public void setAlertePrixEleve(Boolean alertePrixEleve) {
		this.alertePrixEleve = alertePrixEleve;
	}
	public Double getMinPrixConcurrent() {
		return minPrixConcurrent;
	}
	public void setMinPrixConcurrent(Double minPrixConcurrent) {
		this.minPrixConcurrent = minPrixConcurrent;
	}
	public Double getMaxPrixConcurrent() {
		return maxPrixConcurrent;
	}
	public void setMaxPrixConcurrent(Double maxPrixConcurrent) {
		this.maxPrixConcurrent = maxPrixConcurrent;
	}
	public Double getMargeActuelle() {
		return margeActuelle;
	}
	public void setMargeActuelle(Double margeActuelle) {
		this.margeActuelle = margeActuelle;
	}
    

}
