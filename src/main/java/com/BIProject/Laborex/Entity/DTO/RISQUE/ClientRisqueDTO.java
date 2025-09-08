package com.BIProject.Laborex.Entity.DTO.RISQUE;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClientRisqueDTO {
    private String codeClient;
    private String nomClient;
    @JsonSerialize(using= PercentageSerializer.class)
    private double probaIn;  // probabilité d’inactivité
    @JsonSerialize(using= PercentageSerializer.class)
    private double freqAchat;
    private int moisIn; // mois de référence
    @JsonSerialize(using= DoubleSerializer.class)
    private double caCumul;
    private SegmentClientRisque segment;
	public ClientRisqueDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ClientRisqueDTO(String codeClient, String nomClient, double probaIn, double freqAchat, int moisIn,
			double caCumul, SegmentClientRisque segment) {
		super();
		this.codeClient = codeClient;
		this.nomClient = nomClient;
		this.probaIn = probaIn;
		this.freqAchat = freqAchat;
		this.moisIn = moisIn;
		this.caCumul = caCumul;
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
	public double getProbaIn() {
		return probaIn;
	}
	public void setProbaIn(double probaIn) {
		this.probaIn = probaIn;
	}
	public double getFreqAchat() {
		return freqAchat;
	}
	public void setFreqAchat(double freqAchat) {
		this.freqAchat = freqAchat;
	}
	public int getMoisIn() {
		return moisIn;
	}
	public void setMoisIn(int moisIn) {
		this.moisIn = moisIn;
	}
	public double getCaCumul() {
		return caCumul;
	}
	public void setCaCumul(double caCumul) {
		this.caCumul = caCumul;
	}
	public SegmentClientRisque getSegment() {
		return segment;
	}
	public void setSegment(SegmentClientRisque segment) {
		this.segment = segment;
	}
    
}
