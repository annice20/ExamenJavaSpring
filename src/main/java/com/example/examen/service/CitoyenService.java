package com.example.examen.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.examen.model.Citoyen;
import com.example.examen.repository.CitoyenRepository;

@Service
public class CitoyenService {
	
	private final CitoyenRepository citoyenRepository;

    public CitoyenService(CitoyenRepository citoyenRepository) {
        this.citoyenRepository = citoyenRepository;
    }

    public Citoyen creerCitoyen(Citoyen citoyen) {
        return citoyenRepository.save(citoyen);
    }

    public List<Citoyen> obtenirTousCitoyens() {
        return citoyenRepository.findAll();
    }

    public Citoyen obtenirCitoyen(Long id) {
        return citoyenRepository.findById(id).orElseThrow();
    }

    public Citoyen modifierCitoyen(Long id, Citoyen citoyen) {

        Citoyen c = obtenirCitoyen(id);

        c.setNom(citoyen.getNom());
        c.setPrenom(citoyen.getPrenom());
        c.setAdresse(citoyen.getAdresse());

        return citoyenRepository.save(c);
    }

    public void supprimerCitoyen(Long id) {
        citoyenRepository.deleteById(id);
    }
    
    public List<Citoyen> chercherCitoyens(String motCle) {
        if (motCle != null && !motCle.trim().isEmpty()) {
            return citoyenRepository.searchCitoyens(motCle);
        }
        return citoyenRepository.findAll();
    }


}
