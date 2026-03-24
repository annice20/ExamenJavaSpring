package com.example.examen.repository;

import com.example.examen.model.Demande;
import com.example.examen.model.StatutDemande;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DemandeRepository extends JpaRepository<Demande, Long> {

    long countByStatut(StatutDemande statut);
    
    @Query("SELECT COUNT(d) FROM Demande d WHERE FUNCTION('MONTH', d.dateDepot) = :month")
    long countByMonth(int month);
    
    @Query("SELECT COUNT(d) FROM Demande d WHERE d.dateDepot < :dateLimite AND d.statut != :statut")
    long countDossiersEnRetard(LocalDateTime dateLimite, StatutDemande statut);
}