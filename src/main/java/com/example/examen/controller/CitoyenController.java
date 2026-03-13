package com.example.examen.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examen.model.Citoyen;
import com.example.examen.service.CitoyenService;

@RestController
@RequestMapping("/api/citoyens")
public class CitoyenController {
	
	private final CitoyenService citoyenService;

    public CitoyenController(CitoyenService citoyenService) {
        this.citoyenService = citoyenService;
    }

    @PostMapping
    public Citoyen creerCitoyen(@RequestBody Citoyen citoyen) {
        return citoyenService.creerCitoyen(citoyen);
    }

    @GetMapping
    public List<Citoyen> obtenirCitoyens() {
        return citoyenService.obtenirTousCitoyens();
    }

    @GetMapping("/{id}")
    public Citoyen obtenirCitoyen(@PathVariable Long id) {
        return citoyenService.obtenirCitoyen(id);
    }

    @PutMapping("/{id}")
    public Citoyen modifierCitoyen(@PathVariable Long id, @RequestBody Citoyen citoyen) {
        return citoyenService.modifierCitoyen(id, citoyen);
    }

    @DeleteMapping("/{id}")
    public void supprimerCitoyen(@PathVariable Long id) {
        citoyenService.supprimerCitoyen(id);
    }
}
