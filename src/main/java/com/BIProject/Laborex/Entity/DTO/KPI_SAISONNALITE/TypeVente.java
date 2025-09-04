package com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE;

public enum TypeVente {
	FORTE("Période de forte croissance", "#28a745", "Les ventes sont nettement au-dessus de la moyenne. C'est un mois ou une période de haute saison. Il est crucial d'anticiper la demande et de maximiser les stocks et les efforts marketing."),
    STABLE("Période stable", "#ffc107", " Les ventes sont conformes à la moyenne. C'est une période de performance standard."),
    FAIBLE("Partenaire émergent", "#fd7e14", "Les ventes sont en-dessous de la moyenne. C'est un mois de basse saison. Cela pourrait être le moment idéal pour lancer des promotions ciblées ou faire des liquidations.");

    private final String libelle;
    private final String couleur;
    private final String description;

    TypeVente(String libelle, String couleur, String description) {
        this.libelle = libelle;
        this.couleur = couleur;
        this.description = description;
    }

    public String getLibelle() { return libelle; }
    public String getCouleur() { return couleur; }
    public String getDescription() { return description; }
}
