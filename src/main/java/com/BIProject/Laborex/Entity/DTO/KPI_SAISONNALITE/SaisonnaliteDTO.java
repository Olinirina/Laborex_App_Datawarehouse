package com.BIProject.Laborex.Entity.DTO.KPI_SAISONNALITE;

import java.util.List;

public class SaisonnaliteDTO {
	private List<TendanceVenteDTO> tendances;
	private List<GraphiqueVente> graphiqueVentes;
	public SaisonnaliteDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SaisonnaliteDTO(List<TendanceVenteDTO> tendances, List<GraphiqueVente> graphiqueVentes) {
		super();
		this.tendances = tendances;
		this.graphiqueVentes = graphiqueVentes;
	}
	public List<TendanceVenteDTO> getTendances() {
		return tendances;
	}
	public void setTendances(List<TendanceVenteDTO> tendances) {
		this.tendances = tendances;
	}
	public List<GraphiqueVente> getGraphiqueVentes() {
		return graphiqueVentes;
	}
	public void setGraphiqueVentes(List<GraphiqueVente> graphiqueVentes) {
		this.graphiqueVentes = graphiqueVentes;
	}
	

}
