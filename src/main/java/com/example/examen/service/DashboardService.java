package com.example.examen.service;

import com.example.examen.model.StatutDemande;
import com.example.examen.repository.CitoyenRepository;
import com.example.examen.repository.DemandeRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DashboardService {

    private final CitoyenRepository citoyenRepository;
    private final DemandeRepository demandeRepository;

    public DashboardService(CitoyenRepository citoyenRepository,
                            DemandeRepository demandeRepository) {
        this.citoyenRepository = citoyenRepository;
        this.demandeRepository = demandeRepository;
    }

    // INDICATEURS
    public long totalCitoyens() {
        return citoyenRepository.count();
    }

    public long totalDemandes() {
        return demandeRepository.count();
    }

    public long demandesEnAttente() {
        return demandeRepository.countByStatut(StatutDemande.EN_ATTENTE);
    }

    public long demandesValidees() {
        return demandeRepository.countByStatut(StatutDemande.VALIDEE);
    }

    public long cartesDelivrees() {
        return demandeRepository.countByStatut(StatutDemande.IMPRIMEE);
    }

    // PIE CHART
    public Map<String, Long> statsParStatut() {
        Map<String, Long> stats = new HashMap<>();

        for (StatutDemande s : StatutDemande.values()) {
            stats.put(s.name(), demandeRepository.countByStatut(s));
        }

        return stats;
    }

    // STAT MENSUELLES
    public List<Long> statsMensuelles() {
        List<Long> data = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            data.add(demandeRepository.countByMonth(i));
        }

        return data;
    }

    // PAR REGION
    public Map<String, Long> statsParRegion() {

        Map<String, Long> map = new HashMap<>();

        for (Object[] row : citoyenRepository.countByRegionRaw()) {
            map.put((String) row[0], (Long) row[1]);
        }

        return map;
    }

    // DOSSIERS EN RETARD (> 7 jours)
    public long dossiersEnRetard() {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(7);
        return demandeRepository.countDossiersEnRetard(dateLimite, StatutDemande.IMPRIMEE);
    }
}