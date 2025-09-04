package com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM;

public enum CategorieRFMClient {
	CHAMPION("Champions","couleur","Clients très récents, fréquents et qui dépensent beaucoup. Ce sont vos meilleurs clients. Fédérez-les et offrez-leur des récompenses exclusivesClients très récents, fréquents et qui dépensent beaucoup. Ce sont vos meilleurs clients. Fédérez-les et offrez-leur des récompenses exclusives."),
	FIDELE("Clients fidèles","couleur","Clients très fidèles qui achètent souvent et récemment. Ils sont la base de votre business. Assurez-vous de leur offrir une excellente expérience."),
	NOUVEAU("Nouveaux clients","couleur"," Clients récents qui ont beaucoup dépensé. Ils ont un fort potentiel de devenir des champions. Encouragez-les à revenir en leur proposant des offres pertinentes."),
	RISQUE("À risque","couleur","Clients qui étaient fréquents et dépensiers, mais qui n'ont pas acheté récemment. Ils sont en danger de désaffection. Tentez de les réactiver avec des offres spéciales."),
	ENDORMI("Clients endormis","couleur","Clients qui n'ont pas acheté depuis longtemps et qui n'étaient ni fréquents ni dépensiers. Il est très difficile de les réactiver. Ciblez-les avec des promotions de masse ou laissez-les de côté."),
	AUTRES("Autres","Couleur","Ce client ne correspond pas à un segment majeur. Son comportement d'achat est plus difficile à catégoriser. Une analyse plus fine est nécessaire");
	private final String libelle;
    private final String couleur;
    private final String description;
    
    CategorieRFMClient(String libelle, String couleur, String description) {
        this.libelle = libelle;
        this.couleur = couleur;
        this.description= description;    }
    
    public String getLibelle() { return libelle; }
    public String getCouleur() { return couleur; }
    public String getDescription() { return description; }

}
