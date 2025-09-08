package com.BIProject.Laborex.Entity.DTO.RISQUE;

import java.util.List;

import com.BIProject.Laborex.UTIL.PercentageSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class RisqueDTO {
	@JsonSerialize(using= PercentageSerializer.class)
	private double tauxGlobal;
    private List<TopClientsRisques> top5;
    private List<CamembertRisque> camembert;
    private List<ClientRisqueDTO> clients;
	public RisqueDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RisqueDTO(double tauxGlobal, List<TopClientsRisques> top5, List<CamembertRisque> camembert,
			List<ClientRisqueDTO> clients) {
		super();
		this.tauxGlobal = tauxGlobal;
		this.top5 = top5;
		this.camembert = camembert;
		this.clients = clients;
	}
	public double getTauxGlobal() {
		return tauxGlobal;
	}
	public void setTauxGlobal(double tauxGlobal) {
		this.tauxGlobal = tauxGlobal;
	}
	public List<TopClientsRisques> getTop5() {
		return top5;
	}
	public void setTop5(List<TopClientsRisques> top5) {
		this.top5 = top5;
	}
	public List<CamembertRisque> getCamembert() {
		return camembert;
	}
	public void setCamembert(List<CamembertRisque> camembert) {
		this.camembert = camembert;
	}
	public List<ClientRisqueDTO> getClients() {
		return clients;
	}
	public void setClients(List<ClientRisqueDTO> clients) {
		this.clients = clients;
	}
	
	

}
