package com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM;

public class GraphiquesRFM {
	private CategorieRFMClient segment;
	private int nombre;
	public GraphiquesRFM() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GraphiquesRFM(CategorieRFMClient segment, int nombre) {
		super();
		this.segment = segment;
		this.nombre = nombre;
	}
	public CategorieRFMClient getSegment() {
		return segment;
	}
	public void setSegment(CategorieRFMClient segment) {
		this.segment = segment;
	}
	public int getNombre() {
		return nombre;
	}
	public void setNombre(int nombre) {
		this.nombre = nombre;
	}
	

}
