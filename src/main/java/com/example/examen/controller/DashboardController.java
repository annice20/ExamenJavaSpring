package com.example.examen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.examen.service.DashboardService;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalCitoyens", dashboardService.totalCitoyens());
        model.addAttribute("cartes", dashboardService.cartesDelivrees());
        model.addAttribute("attente", dashboardService.demandesEnAttente());
        model.addAttribute("validees", dashboardService.demandesValidees());

        model.addAttribute("stats", dashboardService.statsParStatut());
        model.addAttribute("mensuel", dashboardService.statsMensuelles());
        model.addAttribute("regions", dashboardService.statsParRegion());

        model.addAttribute("retard", dashboardService.dossiersEnRetard());

        return "admin/dashboard";
    }
}