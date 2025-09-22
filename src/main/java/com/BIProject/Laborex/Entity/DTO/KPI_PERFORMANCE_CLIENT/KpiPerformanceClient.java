package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_CLIENT;

import java.util.List;

public class KpiPerformanceClient {
	private List<PerformanceClientDTO> clients;
    private List<ScatterPoint> scatterData;
	public KpiPerformanceClient() {
		super();
		// TODO Auto-generated constructor stub
	}
	public KpiPerformanceClient(List<PerformanceClientDTO> clients, List<ScatterPoint> scatterData) {
		super();
		this.clients = clients;
		this.scatterData = scatterData;
	}
	public List<PerformanceClientDTO> getClients() {
		return clients;
	}
	public void setClients(List<PerformanceClientDTO> clients) {
		this.clients = clients;
	}
	public List<ScatterPoint> getScatterData() {
		return scatterData;
	}
	public void setScatterData(List<ScatterPoint> scatterData) {
		this.scatterData = scatterData;
	}
    

}
