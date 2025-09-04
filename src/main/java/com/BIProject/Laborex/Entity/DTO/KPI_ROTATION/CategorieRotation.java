package com.BIProject.Laborex.Entity.DTO.KPI_ROTATION;

public enum CategorieRotation {
	ROTATION_ELEVEE("Élevé", "#28a745","Très populaire et se vend fréquemment. Il est essentiel de s'assurer de sa disponibilité"),      // Vert
    ROTATION_MOYENNE("Moyenne", "#ffc107","Demande stable. Il est important de maintenir un stock suffisant pour répondre à la demande régulière."),      // Jaune
    ROTATION_FAIBLE("Faible", "#fd7e14","Se vend rarement. Il pourrait être judicieux de revoir son stock, de lancer des promotions ou d'envisager son retrait pour éviter les coûts de stockage.");   // Rouge
    
    private final String libelle;
    private final String couleur;
    private final String description;
    
    CategorieRotation(String libelle, String couleur, String description) {
        this.libelle = libelle;
        this.couleur = couleur;
        this.description= description;    }
    
    public String getLibelle() { return libelle; }
    public String getCouleur() { return couleur; }
    public String getDescription() { return description; }

}
