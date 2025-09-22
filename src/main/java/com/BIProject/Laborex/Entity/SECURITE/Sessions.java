package com.BIProject.Laborex.Entity.SECURITE;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class Sessions {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long idSession;
	private String ssid;
	private LocalDateTime createdAt;
	@ManyToOne
	@JoinColumn(name="utilisateur_id")
	private Utilisateur utilisateur;
	public Sessions() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Sessions(String ssid, LocalDateTime createdAt, Utilisateur utilisateur) {
		super();
		this.ssid = ssid;
		this.createdAt = createdAt;
		this.utilisateur = utilisateur;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}
	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}
	

}
