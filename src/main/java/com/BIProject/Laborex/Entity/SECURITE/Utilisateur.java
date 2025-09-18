package com.BIProject.Laborex.Entity.SECURITE;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Utilisateur {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String email;
	private String motDePasse;
	@Enumerated(EnumType.STRING)
	private Role_Utilisateur role;
	
	public Utilisateur() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Utilisateur(String email, String motDePasse, Role_Utilisateur role) {
		super();
		this.email = email;
		this.motDePasse = motDePasse;
		this.role = role;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMotDePasse() {
		return motDePasse;
	}
	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}
	public Role_Utilisateur getRole() {
		return role;
	}
	public void setRole(Role_Utilisateur role) {
		this.role = role;
	}

    
    
	
	

}
