package com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC;

public enum CategorieClassification {
	A("Classe A","couleur","Une part significative du chiffre d'affaires (jusqu'à 80%). Ce sont vos produits phares. Gérez leur stock de manière très rigoureuse et surveillez-les de près"),
	B("Classe B","couleur","Contribue de manière intermédiaire au chiffre d'affaires (entre 80% et 95% cumulé). Ces produits ont un potentiel de croissance. Maintenez un niveau de service élevé mais avec une gestion des stocks moins intensive"),
	C("Classe C","couleur"," Apporte une faible contribution au chiffre d'affaires (après 95% cumulé). Ces produits nécessitent une gestion simplifiée et moins de ressources. Envisagez un stock de sécurité minimal ou des promotions pour les écouler");
	private final String libelle;
    private final String couleur;
    private final String description;
    
    CategorieClassification(String libelle, String couleur, String description) {
        this.libelle = libelle;
        this.couleur = couleur;
        this.description= description;    }
    
    public String getLibelle() { return libelle; }
    public String getCouleur() { return couleur; }
    public String getDescription() { return description; }
	

}
