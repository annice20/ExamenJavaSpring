package com.example.examen.controller.api;

import com.example.examen.model.Demande;
import com.example.examen.model.StatutDemande;
import com.example.examen.model.User;
import com.example.examen.service.DemandeService;
import com.example.examen.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
public class DemandeApiController {

    private final DemandeService demandeService;
    private final NotificationService notificationService;

    public DemandeApiController(DemandeService demandeService, NotificationService notificationService) {
        this.demandeService = demandeService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Demande> getAll(@RequestParam(name = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return demandeService.chercherDemandes(keyword);
        }
        return demandeService.obtenirDemandes();
    }

    // Changer le statut via API et notifier l'utilisateur
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Demande> updateStatut(@PathVariable Long id, @RequestParam StatutDemande statut) {
        Demande demande = demandeService.getById(id);
        if (demande == null) return ResponseEntity.notFound().build();

        demande.setStatut(statut);
        demandeService.save(demande);

        // Notification automatique
        if (demande.getCitoyen() != null && demande.getCitoyen().getUser() != null) {
            User user = demande.getCitoyen().getUser();
            String message = "Votre demande " + demande.getNumeroDossier() + " est désormais : " + statut;
            notificationService.envoyer(user, message);
        }

        return ResponseEntity.ok(demande);
    }
}