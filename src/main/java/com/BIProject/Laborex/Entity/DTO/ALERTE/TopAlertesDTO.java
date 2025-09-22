package com.BIProject.Laborex.Entity.DTO.ALERTE;

public class TopAlertesDTO {
	private TypeAlerte type;
	private String description;
	public TopAlertesDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TopAlertesDTO(TypeAlerte type, String description) {
		super();
		this.type = type;
		this.description = description;
	}
	public TypeAlerte getType() {
		return type;
	}
	public void setType(TypeAlerte type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
