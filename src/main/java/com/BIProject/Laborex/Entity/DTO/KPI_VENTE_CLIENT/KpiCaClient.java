package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_CLIENT;

import java.util.List;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class KpiCaClient {
	@JsonSerialize(using = DoubleSerializer.class)
	private double caGlobal;
	private List<ClientCa> clients; 
    private List<Top5ClientCa> top5;
	public KpiCaClient() {
		super();
		// TODO Auto-generated constructor stub
	}
	public KpiCaClient(double caGlobal, List<ClientCa> clients, List<Top5ClientCa> top5) {
		super();
		this.caGlobal = caGlobal;
		this.clients = clients;
		this.top5 = top5;
	}
	public double getCaGlobal() {
		return caGlobal;
	}
	public void setCaGlobal(double caGlobal) {
		this.caGlobal = caGlobal;
	}
	public List<ClientCa> getClients() {
		return clients;
	}
	public void setClients(List<ClientCa> clients) {
		this.clients = clients;
	}
	public List<Top5ClientCa> getTop5() {
		return top5;
	}
	public void setTop5(List<Top5ClientCa> top5) {
		this.top5 = top5;
	}
    

}
