package com.BIProject.Laborex.Entity.DTO.KPI_PERFORMANCE_LABO;

import java.util.List;

public class KpiPerformanceLabo {
	private List<PerformanceLaboDTO> labos;
	private List<ScatterPointLabo> scatterData;
	public KpiPerformanceLabo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public KpiPerformanceLabo(List<PerformanceLaboDTO> labos, List<ScatterPointLabo> scatterData) {
		super();
		this.labos = labos;
		this.scatterData = scatterData;
	}
	public List<PerformanceLaboDTO> getLabos() {
		return labos;
	}
	public void setLabos(List<PerformanceLaboDTO> labos) {
		this.labos = labos;
	}
	public List<ScatterPointLabo> getScatterData() {
		return scatterData;
	}
	public void setScatterData(List<ScatterPointLabo> scatterData) {
		this.scatterData = scatterData;
	}
	

}
