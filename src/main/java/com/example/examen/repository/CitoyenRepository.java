package com.example.examen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.examen.model.Citoyen;

public interface CitoyenRepository extends JpaRepository<Citoyen, Long> {
	Optional<Citoyen> findByNumeroNational(String numeroNational);
}
