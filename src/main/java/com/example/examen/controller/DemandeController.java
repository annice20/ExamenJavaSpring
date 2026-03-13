package com.example.examen.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examen.model.Demande;
import com.example.examen.model.StatutDemande;
import com.example.examen.service.DemandeService;

@RestController
@RequestMapping("/api/demandes")
public class DemandeController {
	
	private final DemandeService demandeService;

    public DemandeController(DemandeService demandeService) {
        this.demandeService = demandeService;
    }

    @PostMapping
    public Demande creerDemande(@RequestBody Demande demande) {
        return demandeService.creerDemande(demande);
    }

    @GetMapping
    public List<Demande> obtenirDemandes() {
        return demandeService.obtenirDemandes();
    }

    @PutMapping("/{id}/statut")
    public Demande modifierStatut(@PathVariable Long id, @RequestBody StatutDemande statut) {
        return demandeService.modifierStatut(id, statut);
    }
}
