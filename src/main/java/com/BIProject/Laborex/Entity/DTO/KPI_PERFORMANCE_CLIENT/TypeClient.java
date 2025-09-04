package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT;

public enum TypeClient {
	 	VIP("Client VIP", "#28a745", "Chiffre d'affaires élevé et achats fréquents. À fidéliser absolument."),
	    REGULIER("Client régulier", "#ffc107", "Bon potentiel de croissance, achats réguliers."),
	    OCCASIONNEL("Client occasionnel", "#fd7e14", "Faible contribution, opportunité via des offres ciblées.");

	    private final String libelle;
	    private final String couleur;
	    private final String description;

	    TypeClient(String libelle, String couleur, String description) {
	        this.libelle = libelle;
	        this.couleur = couleur;
	        this.description = description;
	    }

	    public String getLibelle() { return libelle; }
	    public String getCouleur() { return couleur; }
	    public String getDescription() { return description; }
}
