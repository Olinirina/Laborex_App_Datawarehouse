package com.BIProject.Laborex.Entity.DTO.KPI_ROTATION;

import java.util.List;

public class KpiRotation {
	private List<FrequenceVente> ventes; 
    private List<TopArticleRotation> top5;
	public KpiRotation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public KpiRotation(List<FrequenceVente> ventes, List<TopArticleRotation> top5) {
		super();
		this.ventes = ventes;
		this.top5 = top5;
	}
	public List<FrequenceVente> getVentes() {
		return ventes;
	}
	public void setVentes(List<FrequenceVente> ventes) {
		this.ventes = ventes;
	}
	public List<TopArticleRotation> getTop5() {
		return top5;
	}
	public void setTop5(List<TopArticleRotation> top5) {
		this.top5 = top5;
	}
    

}
