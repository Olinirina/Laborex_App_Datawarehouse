package com.BIProject.Laborex.Entity.DTO.COMPARAISON;

public enum StatutCompar {
	PLUS_CHER("Plus cher"),
    EGAL("Egal"),
    MOINS_CHER("Moins cher");
    
    private final String libelle;
    
    StatutCompar(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }

}
