package com.BIProject.Laborex.Entity.DTO.RISQUE;

public enum SegmentClientRisque {
    HIGH("High", "#E74C3C", 
         "Clients avec une probabilité très élevée d’inactivité. Ce sont vos clients les plus critiques : agissez vite avec des offres ciblées."),
    MEDIUM("Medium", "#F1C40F", 
         "Clients avec un risque modéré d’inactivité. Surveillez-les et proposez-leur des incitations pour maintenir leur fidélité."),
    LOW("Low", "#2ECC71", 
         "Clients avec une faible probabilité d’inactivité. Continuez à les engager régulièrement pour consolider la relation.");

    private final String libelle;
    private final String couleur;
    private final String description;

    SegmentClientRisque(String libelle, String couleur, String description) {
        this.libelle = libelle;
        this.couleur = couleur;
        this.description = description;
    }

    public String getLibelle() { return libelle; }
    public String getCouleur() { return couleur; }
    public String getDescription() { return description; }
}

