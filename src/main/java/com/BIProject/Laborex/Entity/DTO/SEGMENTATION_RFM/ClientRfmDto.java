package com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM;

public class ClientRfmDto {
	private String codeClient;
	private String nomClient;
	private long recence; // Nombre de jours depuis la dernière commande
    private long frequence; // Nombre total de commandes
    private double montant; // Montant total dépensé
    private int scoreR;
    private int scoreF;
    private int scoreM;
    private CategorieRFMClient segment;
	public ClientRfmDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ClientRfmDto(String codeClient, String nomClient, long recence, long frequence, double montant, int scoreR,
			int scoreF, int scoreM, CategorieRFMClient segment) {
		super();
		this.codeClient = codeClient;
		this.nomClient = nomClient;
		this.recence = recence;
		this.frequence = frequence;
		this.montant = montant;
		this.scoreR = scoreR;
		this.scoreF = scoreF;
		this.scoreM = scoreM;
		this.segment = segment;
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
	public long getRecence() {
		return recence;
	}
	public void setRecence(long recence) {
		this.recence = recence;
	}
	public long getFrequence() {
		return frequence;
	}
	public void setFrequence(long frequence) {
		this.frequence = frequence;
	}
	public double getMontant() {
		return montant;
	}
	public void setMontant(double montant) {
		this.montant = montant;
	}
	public int getScoreR() {
		return scoreR;
	}
	public void setScoreR(int scoreR) {
		this.scoreR = scoreR;
	}
	public int getScoreF() {
		return scoreF;
	}
	public void setScoreF(int scoreF) {
		this.scoreF = scoreF;
	}
	public int getScoreM() {
		return scoreM;
	}
	public void setScoreM(int scoreM) {
		this.scoreM = scoreM;
	}
	public CategorieRFMClient getSegment() {
		return segment;
	}
	public void setSegment(CategorieRFMClient segment) {
		this.segment = segment;
	}
    

}
