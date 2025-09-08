package com.BIProject.Laborex.Entity.DTO.ALERTE;

public enum TypeAlerte {
	STOCK_CRITIQUE("Stock Critique"),
    CLIENT_INACTIF("Client Inactif"),
    ANOMALIE_VENTE("Anomalie de Vente");
    
    private final String libelle;
    
    TypeAlerte(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }

}
