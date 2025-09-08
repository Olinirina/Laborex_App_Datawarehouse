package com.BIProject.Laborex.Entity.DTO.PREVISION;

import java.util.List;

public class PrevisionDTO {
	 private List<DemandePrevisionDTO> tableau;   // toutes les prévisions par article et mois
	 private List<GraphiqueDemandeDTO> graphiques; // top 5 articles, stable, pour graphique
	 public PrevisionDTO() {
		super();
		// TODO Auto-generated constructor stub
	 }
	 public PrevisionDTO(List<DemandePrevisionDTO> tableau, List<GraphiqueDemandeDTO> graphiques) {
		super();
		this.tableau = tableau;
		this.graphiques = graphiques;
	 }
	 public List<DemandePrevisionDTO> getTableau() {
		 return tableau;
	 }
	 public void setTableau(List<DemandePrevisionDTO> tableau) {
		 this.tableau = tableau;
	 }
	 public List<GraphiqueDemandeDTO> getGraphiques() {
		 return graphiques;
	 }
	 public void setGraphiques(List<GraphiqueDemandeDTO> graphiques) {
		 this.graphiques = graphiques;
	 }
	 

}
