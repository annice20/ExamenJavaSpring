package com.example.examen.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examen.service.DashboardService;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard")
    public Map<String, Object> statistiques() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCitoyens", dashboardService.totalCitoyens());
        stats.put("totalDemandes", dashboardService.totalDemandes());
        stats.put("demandesEnAttente", dashboardService.demandesEnAttente());

        return stats;
    }
}