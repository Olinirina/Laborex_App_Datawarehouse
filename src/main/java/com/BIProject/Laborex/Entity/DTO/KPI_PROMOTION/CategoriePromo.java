package com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION;

public enum CategoriePromo {

	SUCCES("Promotion à succès", "#28a745", "Chiffre d'affaires et volume de ventes très élevés. C'est une promotion très performante à renouveler"),
	MARGE("Promotion à forte marge", "#28a745", "Génère un bon chiffre d'affaires malgré un volume de ventes modéré. Cela indique une bonne rentabilité des produits en promotion."),
    VOLUMINEUSE("Promotion volumineuse", "#ffc107", "Génère un grand volume de ventes mais un chiffre d'affaires plus faible. Efficace pour le déstockage ou l'attraction de nouveaux clients.."),
    A_OPTIMISER("Promotion à optimiser", "#fd7e14", "Chiffre d'affaires et volume de ventes modérés. Cette promotion pourrait être améliorée ou remplacée.");

    private final String libelle;
    private final String couleur;
    private final String description;

    CategoriePromo(String libelle, String couleur, String description) {
        this.libelle = libelle;
        this.couleur = couleur;
        this.description = description;
    }

    public String getLibelle() { return libelle; }
    public String getCouleur() { return couleur; }
    public String getDescription() { return description; }
}
