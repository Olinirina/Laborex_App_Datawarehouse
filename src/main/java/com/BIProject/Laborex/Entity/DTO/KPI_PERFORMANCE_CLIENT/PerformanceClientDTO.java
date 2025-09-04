package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT;

import java.time.LocalDate;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PerformanceClientDTO {
		private String codeClient;
	 	private String nomClient;
	 	@JsonSerialize(using = DoubleSerializer.class)
	    private double chiffreAffaires;
	    private long frequenceAchat;
	    private LocalDate derniereCommande;
	    private TypeClient categorie;
		public PerformanceClientDTO() {
			super();
			// TODO Auto-generated constructor stub
		}
		public PerformanceClientDTO(String codeClient,String nomClient, double chiffreAffaires, long frequenceAchat,
				LocalDate derniereCommande, TypeClient categorie) {
			super();
			this.codeClient= codeClient;
			this.nomClient = nomClient;
			this.chiffreAffaires = chiffreAffaires;
			this.frequenceAchat = frequenceAchat;
			this.derniereCommande = derniereCommande;
			this.categorie = categorie;
		}
		public String getNomClient() {
			return nomClient;
		}
		public void setNomClient(String nomClient) {
			this.nomClient = nomClient;
		}
		public double getChiffreAffaires() {
			return chiffreAffaires;
		}
		public void setChiffreAffaires(double chiffreAffaires) {
			this.chiffreAffaires = chiffreAffaires;
		}
		public long getFrequenceAchat() {
			return frequenceAchat;
		}
		public void setFrequenceAchat(long frequenceAchat) {
			this.frequenceAchat = frequenceAchat;
		}
		public LocalDate getDerniereCommande() {
			return derniereCommande;
		}
		public void setDerniereCommande(LocalDate derniereCommande) {
			this.derniereCommande = derniereCommande;
		}
		public TypeClient getCategorie() {
			return categorie;
		}
		public void setCategorie(TypeClient categorie) {
			this.categorie = categorie;
		}
		public String getCodeClient() {
			return codeClient;
		}
		public void setCodeClient(String codeClient) {
			this.codeClient = codeClient;
		}
	    
}
