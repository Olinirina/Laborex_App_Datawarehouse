package com.BIProject.Laborex.Entity.DTO.CLASSIFICATION_ABC;

import java.util.List;

public class ClassificationABCDTO {
	private List<AbcDto> abcDto;
	private List<CamembertClassification> camembert;
	public ClassificationABCDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ClassificationABCDTO(List<AbcDto> abcDto, List<CamembertClassification> camembert) {
		super();
		this.abcDto = abcDto;
		this.camembert = camembert;
	}
	public List<AbcDto> getAbcDto() {
		return abcDto;
	}
	public void setAbcDto(List<AbcDto> abcDto) {
		this.abcDto = abcDto;
	}
	public List<CamembertClassification> getCamembert() {
		return camembert;
	}
	public void setCamembert(List<CamembertClassification> camembert) {
		this.camembert = camembert;
	}
	

}
