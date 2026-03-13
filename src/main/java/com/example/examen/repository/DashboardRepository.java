package com.example.examen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.examen.model.Citoyen;

@Repository
public interface DashboardRepository extends JpaRepository<Citoyen, Long> {

    @Query("SELECT COUNT(c) FROM Citoyen c")
    long totalCitoyens();

}