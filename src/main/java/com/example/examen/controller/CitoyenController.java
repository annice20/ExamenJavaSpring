package com.example.examen.controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.examen.model.Citoyen;
import com.example.examen.model.User;
import com.example.examen.service.CitoyenService;
import com.example.examen.service.NotificationService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/citoyens")
public class CitoyenController {

	@Autowired
    private final CitoyenService citoyenService;
	
    @Autowired
    private NotificationService notificationService;

    public CitoyenController(CitoyenService citoyenService) {
        this.citoyenService = citoyenService;
    }

    
    @GetMapping("/liste")
    public String listeCitoyens(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Citoyen> citoyens;
        
        if (keyword != null) {
            citoyens = citoyenService.chercherCitoyens(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            citoyens = citoyenService.obtenirTousCitoyens();
        }
        
        model.addAttribute("citoyens", citoyens);
        
        // Pour vos notifications (simulé)
        User currentUser = new User();
        currentUser.setId(1L);
        model.addAttribute("notifications", notificationService.getUserNotifications(currentUser));

        return "citoyens/lesCitoyens";
    }

    
    @GetMapping("/creer")
    public String ajouterCitoyenForm(Model model) {
        model.addAttribute("citoyen", new Citoyen());
        return "citoyens/ajout";
    }

    @PostMapping("/ajouter")
    public String ajouterCitoyen(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("numeroNational") String numeroNational,
            @RequestParam(value = "sexe", required = false) String sexe,
            @RequestParam(value = "dateNaissance", required = false) String dateNaissance,
            @RequestParam(value = "lieuNaissance", required = false) String lieuNaissance,
            @RequestParam(value = "profession", required = false) String profession,
            @RequestParam(value = "adresse", required = false) String adresse,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            HttpSession session) {

        Citoyen citoyen = new Citoyen();

        citoyen.setNom(nom);
        citoyen.setPrenom(prenom);
        citoyen.setNumeroNational(numeroNational);
        citoyen.setSexe(sexe);
        citoyen.setLieuNaissance(lieuNaissance);
        citoyen.setProfession(profession);
        citoyen.setAdresse(adresse);

        if (dateNaissance != null && !dateNaissance.isEmpty()) {
            citoyen.setDateNaissance(java.time.LocalDate.parse(dateNaissance));
        }

        // LIER USER CONNECTÉ
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            User user = new User();
            user.setId(userId);
            citoyen.setUser(user);
        }

        // Upload image (inchangé)
        if (photo != null && !photo.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);

                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                citoyen.setPhoto(filename);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        citoyenService.creerCitoyen(citoyen);

        return "redirect:/citoyens/liste";
    }
    
    @GetMapping("/edit/{id}")
    public String editerCitoyenForm(@PathVariable Long id, Model model) {
        Citoyen citoyen = citoyenService.obtenirCitoyen(id);
        if (citoyen == null) {
            return "redirect:/citoyens/liste";
        }
        model.addAttribute("citoyen", citoyen);
        return "citoyens/modifier";
    }
    
    @PostMapping("/update/{id}")
    public String modifierCitoyen(@PathVariable Long id, 
                                 @ModelAttribute Citoyen citoyenDetails,
                                 @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {
        
        Citoyen citoyenExistant = citoyenService.obtenirCitoyen(id);
        
        if (citoyenExistant != null) {
            // Mise à jour des champs texte
            citoyenExistant.setNom(citoyenDetails.getNom());
            citoyenExistant.setPrenom(citoyenDetails.getPrenom());
            citoyenExistant.setNumeroNational(citoyenDetails.getNumeroNational());
            citoyenExistant.setSexe(citoyenDetails.getSexe());
            citoyenExistant.setAdresse(citoyenDetails.getAdresse());

            // Gestion de la nouvelle photo
            if (photoFile != null && !photoFile.isEmpty()) {
                try {
                    String uploadDir = System.getProperty("user.dir") + "/uploads";
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String filename = UUID.randomUUID() + "_" + photoFile.getOriginalFilename();
                    Path filePath = uploadPath.resolve(filename);

                    Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    citoyenExistant.setPhoto(filename);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            citoyenService.creerCitoyen(citoyenExistant);
        }
        
        return "redirect:/citoyens/liste";
    }
    
    @GetMapping("/delete/{id}")
    public String supprimerCitoyenRedirect(@PathVariable Long id) {
        citoyenService.supprimerCitoyen(id);
        return "redirect:/citoyens/liste";
    }
}