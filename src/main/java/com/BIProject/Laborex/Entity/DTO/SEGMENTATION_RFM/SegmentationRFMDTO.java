package com.BIProject.Laborex.Entity.DTO.SEGMENTATION_RFM;

import java.util.List;

public class SegmentationRFMDTO {
	private List<ClientRfmDto> clients;
	private List<GraphiquesRFM> graphiques;
	public SegmentationRFMDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SegmentationRFMDTO(List<ClientRfmDto> clients, List<GraphiquesRFM> graphiques) {
		super();
		this.clients = clients;
		this.graphiques = graphiques;
	}
	public List<ClientRfmDto> getClients() {
		return clients;
	}
	public void setClients(List<ClientRfmDto> clients) {
		this.clients = clients;
	}
	public List<GraphiquesRFM> getGraphiques() {
		return graphiques;
	}
	public void setGraphiques(List<GraphiquesRFM> graphiques) {
		this.graphiques = graphiques;
	}
	

}
