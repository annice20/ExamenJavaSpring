package com.example.examen.controller;

import com.example.examen.model.*;
import com.example.examen.service.CitoyenService;
import com.example.examen.service.DemandeService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeService demandeService;
    private final CitoyenService citoyenService;

    public DemandeController(DemandeService demandeService, CitoyenService citoyenService) {
        this.demandeService = demandeService;
        this.citoyenService = citoyenService;
    }

    // Liste
    @GetMapping("/listedemande")
    public String liste(Model model) {
        model.addAttribute("demandes", demandeService.obtenirDemandes());
        model.addAttribute("types", TypeDemande.values());
        return "demande/lesdemandes";
    }
    
    @GetMapping("/creerdemande/{citoyenId}")
    public String ajouterDemandeForm(@PathVariable Long citoyenId, Model model) {
    	Demande demande = new Demande();

        Citoyen citoyen = citoyenService.obtenirCitoyen(citoyenId);

        demande.setCitoyen(citoyen);

        model.addAttribute("demande", demande);
        model.addAttribute("types", TypeDemande.values());
        return "demande/ajoutdemande";
    }

    // Création
    @PostMapping("/create")
    public String creer(@ModelAttribute Demande demande, HttpSession session) {

        // Vérifier citoyen
        if (demande.getCitoyen() == null) {
            throw new RuntimeException("Citoyen obligatoire");
        }

        // Récupérer l'utilisateur connecté
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Utilisateur non connecté");
        }

        User agent = new User();
        agent.setId(userId);

        // Lier l'agent à la demande
        demande.setAgentResponsable(agent);

        demandeService.creerDemande(demande);

        return "redirect:/demandes/listedemande";
    }

    // Changer statut
    @GetMapping("/statut/{id}/{statut}")
    public String changerStatut(@PathVariable Long id,
                               @PathVariable StatutDemande statut) {
        demandeService.modifierStatut(id, statut);
        return "redirect:/demandes";
    }
}