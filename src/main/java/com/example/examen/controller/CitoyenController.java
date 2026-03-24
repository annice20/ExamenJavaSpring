package com.example.examen.controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.examen.model.Citoyen;
import com.example.examen.service.CitoyenService;

@Controller
@RequestMapping("/citoyens")
public class CitoyenController {

    private final CitoyenService citoyenService;

    public CitoyenController(CitoyenService citoyenService) {
        this.citoyenService = citoyenService;
    }

    
    @GetMapping("/liste")
    public String listeCitoyens(Model model) {
        model.addAttribute("citoyens", citoyenService.obtenirTousCitoyens());
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
            @RequestParam(value = "photo", required = false) MultipartFile photo) {

        System.out.println("=== OK CONTROLLER ===");

        Citoyen citoyen = new Citoyen();
        citoyen.setNom(nom);
        citoyen.setPrenom(prenom);
        citoyen.setNumeroNational(numeroNational);
        citoyen.setSexe(sexe);
        citoyen.setLieuNaissance(lieuNaissance);
        citoyen.setProfession(profession);
        citoyen.setAdresse(adresse);

        // Conversion date
        if (dateNaissance != null && !dateNaissance.isEmpty()) {
            citoyen.setDateNaissance(java.time.LocalDate.parse(dateNaissance));
        }

        // Upload image
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

                System.out.println("UPLOAD OK");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        citoyenService.creerCitoyen(citoyen);

        return "redirect:/citoyens/liste";
    }
    
    // API
    @GetMapping("/api")
    @ResponseBody
    public List<Citoyen> apiCitoyens() {
        return citoyenService.obtenirTousCitoyens();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Citoyen obtenirCitoyen(@PathVariable Long id) {
        return citoyenService.obtenirCitoyen(id);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void supprimerCitoyen(@PathVariable Long id) {
        citoyenService.supprimerCitoyen(id);
    }
}