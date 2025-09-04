package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO;

public enum TypeLabo {
	STRATEGIQUE("Partenaire stratégiqu", "#28a745", "Ce laboratoire génère un chiffre d'affaires très élevé avec une offre de produits diversifiée. Il est un partenaire clé."),
    FIABLE("Partenaire fiable", "#ffc107", "Ce laboratoire a une contribution solide au chiffre d'affaires et une gamme de produits raisonnable. Un partenaire sur lequel on peut compter."),
    NICHE("Partenaire émergent", "#fd7e14", "Ce laboratoire a une contribution modérée. Il peut s'agir d'un nouveau partenaire à développer ou d'un acteur de niche.");

    private final String libelle;
    private final String couleur;
    private final String description;

    TypeLabo(String libelle, String couleur, String description) {
        this.libelle = libelle;
        this.couleur = couleur;
        this.description = description;
    }

    public String getLibelle() { return libelle; }
    public String getCouleur() { return couleur; }
    public String getDescription() { return description; }
}
