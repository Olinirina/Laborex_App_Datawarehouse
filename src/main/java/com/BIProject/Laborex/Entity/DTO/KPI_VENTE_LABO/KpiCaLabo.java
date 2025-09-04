package com.BIProject.Laborex.Entity.DTO.KPI_VENTE_LABO;

import java.util.List;

import com.BIProject.Laborex.UTIL.DoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class KpiCaLabo {
	@JsonSerialize(using = DoubleSerializer.class)
	private double caGlobal;
	private List<LaboCa> labos; 
    private List<Top5LaboCa> top5;
	public KpiCaLabo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public KpiCaLabo(double caGlobal, List<LaboCa> labos, List<Top5LaboCa> top5) {
		super();
		this.caGlobal = caGlobal;
		this.labos = labos;
		this.top5 = top5;
	}
	public double getCaGlobal() {
		return caGlobal;
	}
	public void setCaGlobal(double caGlobal) {
		this.caGlobal = caGlobal;
	}
	public List<LaboCa> getLabos() {
		return labos;
	}
	public void setLabos(List<LaboCa> labos) {
		this.labos = labos;
	}
	public List<Top5LaboCa> getTop5() {
		return top5;
	}
	public void setTop5(List<Top5LaboCa> top5) {
		this.top5 = top5;
	}
    

}
