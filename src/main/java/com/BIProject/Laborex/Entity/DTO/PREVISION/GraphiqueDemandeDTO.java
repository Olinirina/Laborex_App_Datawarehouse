package com.BIProject.Laborex.Entity.DTO.PREVISION;

public class GraphiqueDemandeDTO {
	 private String codeArticle;
	    private String libelle;
	    private double quantiteJan;
	    private double quantiteFev;
	    private double quantiteMar;
		public GraphiqueDemandeDTO() {
			super();
			// TODO Auto-generated constructor stub
		}
		public GraphiqueDemandeDTO(String codeArticle, String libelle, double quantiteJan, double quantiteFev,
				double quantiteMar) {
			super();
			this.codeArticle = codeArticle;
			this.libelle = libelle;
			this.quantiteJan = quantiteJan;
			this.quantiteFev = quantiteFev;
			this.quantiteMar = quantiteMar;
		}
		public String getCodeArticle() {
			return codeArticle;
		}
		public void setCodeArticle(String codeArticle) {
			this.codeArticle = codeArticle;
		}
		public String getLibelle() {
			return libelle;
		}
		public void setLibelle(String libelle) {
			this.libelle = libelle;
		}
		public double getQuantiteJan() {
			return quantiteJan;
		}
		public void setQuantiteJan(double quantiteJan) {
			this.quantiteJan = quantiteJan;
		}
		public double getQuantiteFev() {
			return quantiteFev;
		}
		public void setQuantiteFev(double quantiteFev) {
			this.quantiteFev = quantiteFev;
		}
		public double getQuantiteMar() {
			return quantiteMar;
		}
		public void setQuantiteMar(double quantiteMar) {
			this.quantiteMar = quantiteMar;
		}
	    

}
