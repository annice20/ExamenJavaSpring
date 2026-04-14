package com.example.examen.controller.api;

import com.example.examen.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardApiController {

    @Autowired private DashboardService dashboardService;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCitoyens", dashboardService.totalCitoyens());
        stats.put("cartesDelivrees", dashboardService.cartesDelivrees());
        stats.put("attente", dashboardService.demandesEnAttente());
        stats.put("validees", dashboardService.demandesValidees());
        stats.put("statsParStatut", dashboardService.statsParStatut());
        stats.put("regions", dashboardService.statsParRegion());
        return stats;
    }
}