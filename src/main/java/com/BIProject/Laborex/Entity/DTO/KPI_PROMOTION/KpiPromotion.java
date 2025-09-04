package com.BIProject.Laborex.Entity.DTO.KPI_PROMOTION;

import java.util.List;

public class KpiPromotion {
	private List<RecapPromoDTO> promos;
	private List<ScatterPointPromotion> scatterData;
	public KpiPromotion() {
		super();
		// TODO Auto-generated constructor stub
	}
	public KpiPromotion(List<RecapPromoDTO> promos, List<ScatterPointPromotion> scatterData) {
		super();
		this.promos = promos;
		this.scatterData = scatterData;
	}
	public List<RecapPromoDTO> getPromos() {
		return promos;
	}
	public void setPromos(List<RecapPromoDTO> promos) {
		this.promos = promos;
	}
	public List<ScatterPointPromotion> getScatterData() {
		return scatterData;
	}
	public void setScatterData(List<ScatterPointPromotion> scatterData) {
		this.scatterData = scatterData;
	}
	

}
