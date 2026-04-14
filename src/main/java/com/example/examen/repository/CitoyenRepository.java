package com.example.examen.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.examen.model.Citoyen;

public interface CitoyenRepository extends JpaRepository<Citoyen, Long> {
	Optional<Citoyen> findByNumeroNational(String numeroNational);
	
	@Query("SELECT c.adresse, COUNT(c) FROM Citoyen c GROUP BY c.adresse")
	List<Object[]> countByRegionRaw();
	
	// Recherche par nom, prénom ou numéro national
    @Query("SELECT c FROM Citoyen c WHERE " +
           "LOWER(c.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.prenom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.numeroNational) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Citoyen> searchCitoyens(String query);
}
