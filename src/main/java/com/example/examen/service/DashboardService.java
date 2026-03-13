package com.example.examen.service;

import org.springframework.stereotype.Service;

import com.example.examen.repository.CitoyenRepository;
import com.example.examen.repository.DemandeRepository;

@Service
public class DashboardService {

    private final CitoyenRepository citoyenRepository;
    private final DemandeRepository demandeRepository;

    public DashboardService(CitoyenRepository citoyenRepository,
                            DemandeRepository demandeRepository) {

        this.citoyenRepository = citoyenRepository;
        this.demandeRepository = demandeRepository;
    }

    public long totalCitoyens() {
        return citoyenRepository.count();
    }

    public long totalDemandes() {
        return demandeRepository.count();
    }

    public long demandesEnAttente() {
        return demandeRepository.countByStatut("EN_ATTENTE");
    }

}