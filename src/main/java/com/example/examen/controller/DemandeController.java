package com.example.examen.controller;

import com.example.examen.model.*;
import com.example.examen.service.CitoyenService;
import com.example.examen.service.DemandeService;
import com.example.examen.service.NotificationService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeService demandeService;
    private final CitoyenService citoyenService;
    private final NotificationService notificationService;

    public DemandeController(DemandeService demandeService, CitoyenService citoyenService, NotificationService notificationService) {
        this.demandeService = demandeService;
        this.citoyenService = citoyenService;
        this.notificationService = notificationService;
    }

    // Liste
    @GetMapping("/listedemande")
    public String liste(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Demande> demandes;
        
        if (keyword != null && !keyword.isEmpty()) {
            demandes = demandeService.chercherDemandes(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            demandes = demandeService.obtenirDemandes();
        }
        
        model.addAttribute("demandes", demandes);
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
    public String creer(@ModelAttribute Demande demande,
                        HttpSession session,
                        Model model) {

        // Vérifier citoyen
        if (demande.getCitoyen() == null) {
            model.addAttribute("error", "Citoyen obligatoire");
            model.addAttribute("types", TypeDemande.values());
            return "demande/ajoutdemande";
        }

        // Récupérer l'utilisateur connecté
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            model.addAttribute("error", "Utilisateur non connecté");
            return "redirect:/login";
        }

        User agent = new User();
        agent.setId(userId);

        // Lier l'agent
        demande.setAgentResponsable(agent);

        demandeService.creerDemande(demande);

        // Message succès
        model.addAttribute("success", "Demande ajoutée avec succès !");
        model.addAttribute("types", TypeDemande.values());

        // Réinitialiser formulaire
        model.addAttribute("demande", new Demande());

        return "demande/ajoutdemande";
    }

    // Changer statut
    @GetMapping("/statut/{id}/{statut}")
    public String changerStatut(@PathVariable Long id,
                               @PathVariable StatutDemande statut,
                               RedirectAttributes redirectAttributes) {

        // Vérifier si la demande existe
        Demande demande = demandeService.getById(id);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable !");
            return "redirect:/demandes/listedemande";
        }

        // Modifier le statut
        demande.setStatut(statut);
        demandeService.save(demande);

        // Récupérer le user lié au citoyen
        User user = null;

        if (demande.getCitoyen() != null) {
            user = demande.getCitoyen().getUser();
        }

        // Envoyer notification si user existe
        if (user != null) {

            String message;

            if (statut == StatutDemande.VALIDEE) {
                message = "Votre demande " + demande.getNumeroDossier() + " a été VALIDÉE";
                redirectAttributes.addFlashAttribute("success", "Demande validée !");
            } else {
                message = "Votre demande " + demande.getNumeroDossier() + " a été REJETÉE";
                redirectAttributes.addFlashAttribute("error", "Demande rejetée !");
            }

            notificationService.envoyer(user, message);

        } else {
            // Cas où aucun user lié
            redirectAttributes.addFlashAttribute("error",
                    "Statut modifié mais notification non envoyée (aucun utilisateur lié)");
        }

        // Redirection vers la liste
        return "redirect:/demandes/listedemande";
    }
}