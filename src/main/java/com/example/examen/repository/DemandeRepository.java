package com.example.examen.repository;

import com.example.examen.model.Demande;
import com.example.examen.model.StatutDemande;
import com.example.examen.model.TypeDemande;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemandeRepository extends JpaRepository<Demande, Long> {

    long countByStatut(StatutDemande statut);
    
    long countByType(TypeDemande type);
    
    @Query("SELECT COUNT(d) FROM Demande d WHERE FUNCTION('MONTH', d.dateDepot) = :month")
    long countByMonth(int month);
    
    @Query("SELECT COUNT(d) FROM Demande d WHERE d.dateDepot < :dateLimite AND d.statut != :statut")
    long countDossiersEnRetard(LocalDateTime dateLimite, StatutDemande statut);
    
    @Query("SELECT d FROM Demande d JOIN d.citoyen c WHERE " +
            "LOWER(d.numeroDossier) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.prenom) LIKE LOWER(CONCAT('%', :query, '%'))")
     List<Demande> searchDemandes(@Param("query") String query);
}