package com.example.examen.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.examen.model.Demande;
import com.example.examen.model.StatutDemande;
import com.example.examen.repository.DemandeRepository;

@Service
public class DemandeService {

    private final DemandeRepository demandeRepository;

    public DemandeService(DemandeRepository demandeRepository) {
        this.demandeRepository = demandeRepository;
    }

    public Demande creerDemande(Demande demande) {
        demande.setDateDepot(LocalDateTime.now());
        demande.setStatut(StatutDemande.EN_ATTENTE);
        return demandeRepository.save(demande);
    }

    public List<Demande> obtenirDemandes() {
        return demandeRepository.findAll();
    }

    public Demande modifierStatut(Long id, StatutDemande statut) {
        Demande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        demande.setStatut(statut);
        return demandeRepository.save(demande);
    }
    
    public void save(Demande demande) {
        demandeRepository.save(demande);
    }

    public Demande getById(Long id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
    }
}