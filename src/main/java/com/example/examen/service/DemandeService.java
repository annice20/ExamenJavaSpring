package com.example.examen.service;

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
        return demandeRepository.save(demande);
    }

    public List<Demande> obtenirDemandes() {
        return demandeRepository.findAll();
    }

    public Demande modifierStatut(Long id, StatutDemande statut) {

        Demande demande = demandeRepository.findById(id).orElseThrow();

        demande.setStatut(statut);

        return demandeRepository.save(demande);
    }

}
