package com.example.examen.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Demande {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroDossier;

    private LocalDateTime dateDepot;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    @ManyToOne
    private Citoyen citoyen;

    @ManyToOne
    private User agentResponsable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroDossier() {
		return numeroDossier;
	}

	public void setNumeroDossier(String numeroDossier) {
		this.numeroDossier = numeroDossier;
	}

	public LocalDateTime getDateDepot() {
		return dateDepot;
	}

	public void setDateDepot(LocalDateTime dateDepot) {
		this.dateDepot = dateDepot;
	}

	public StatutDemande getStatut() {
		return statut;
	}

	public void setStatut(StatutDemande statut) {
		this.statut = statut;
	}

	public Citoyen getCitoyen() {
		return citoyen;
	}

	public void setCitoyen(Citoyen citoyen) {
		this.citoyen = citoyen;
	}

	public User getAgentResponsable() {
		return agentResponsable;
	}

	public void setAgentResponsable(User agentResponsable) {
		this.agentResponsable = agentResponsable;
	}

}
