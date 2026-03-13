package com.example.examen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.examen.model.Demande;

public interface DemandeRepository extends JpaRepository<Demande, Long> {

    long countByStatut(String statut);

}