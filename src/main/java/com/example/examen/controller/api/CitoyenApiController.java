package com.example.examen.controller.api;

import com.example.examen.model.Citoyen;
import com.example.examen.service.CitoyenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/citoyens")
public class CitoyenApiController {

    private final CitoyenService citoyenService;

    public CitoyenApiController(CitoyenService citoyenService) {
        this.citoyenService = citoyenService;
    }

    // Récupérer tous les citoyens ou filtrer par mot-clé
    @GetMapping
    public List<Citoyen> getAll(@RequestParam(name = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return citoyenService.chercherCitoyens(keyword);
        }
        return citoyenService.obtenirTousCitoyens();
    }

    // Récupérer un citoyen par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Citoyen> getById(@PathVariable Long id) {
        Citoyen citoyen = citoyenService.obtenirCitoyen(id);
        return citoyen != null ? ResponseEntity.ok(citoyen) : ResponseEntity.notFound().build();
    }

    // Créer un citoyen (JSON)
    @PostMapping
    public Citoyen create(@RequestBody Citoyen citoyen) {
        return citoyenService.creerCitoyen(citoyen);
    }

    // Supprimer un citoyen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        citoyenService.supprimerCitoyen(id);
        return ResponseEntity.noContent().build();
    }
}