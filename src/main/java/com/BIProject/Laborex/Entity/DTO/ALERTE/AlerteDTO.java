package com.BIProject.Laborex.Entity.DTO.ALERTE;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class AlerteDTO {
	private String CodeReference;
	private String NomReference;
    private String description;	    
	 @Enumerated(EnumType.STRING)
	    private TypeAlerte type;
	     // Code article, client, vente.	    
	    private Integer valeur; // Quantité, nombre de jours, ....	    
	    @Enumerated(EnumType.STRING)
	    private NiveauSeverite severite;
		public AlerteDTO() {
			super();
			// TODO Auto-generated constructor stub
		}
	
		public AlerteDTO(String codeReference, String nomReference, String description, TypeAlerte type, Integer valeur,
				NiveauSeverite severite) {
			super();
			CodeReference = codeReference;
			NomReference = nomReference;
			this.description = description;
			this.type = type;
			this.valeur = valeur;
			this.severite = severite;
		}

		public TypeAlerte getType() {
			return type;
		}
		public void setType(TypeAlerte type) {
			this.type = type;
		}
		
		public String getNomReference() {
			return NomReference;
		}

		public void setNomReference(String nomReference) {
			NomReference = nomReference;
		}

		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getCodeReference() {
			return CodeReference;
		}
		public void setCodeReference(String codeReference) {
			CodeReference = codeReference;
		}
		public Integer getValeur() {
			return valeur;
		}
		public void setValeur(Integer valeur) {
			this.valeur = valeur;
		}
		public NiveauSeverite getSeverite() {
			return severite;
		}
		public void setSeverite(NiveauSeverite severite) {
			this.severite = severite;
		}
	    

}
