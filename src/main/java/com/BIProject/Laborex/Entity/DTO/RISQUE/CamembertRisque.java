package com.BIProject.Laborex.Entity.DTO.RISQUE;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class CamembertRisque {
	private SegmentClientRisque segment;
    @JsonSerialize(using= PercentageSerializer.class)
    private double pourcentage;
	public CamembertRisque() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CamembertRisque(SegmentClientRisque segment, double pourcentage) {
		super();
		this.segment = segment;
		this.pourcentage = pourcentage;
	}
	public SegmentClientRisque getSegment() {
		return segment;
	}
	public void setSegment(SegmentClientRisque segment) {
		this.segment = segment;
	}
	public double getPourcentage() {
		return pourcentage;
	}
	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}
	
	

}
