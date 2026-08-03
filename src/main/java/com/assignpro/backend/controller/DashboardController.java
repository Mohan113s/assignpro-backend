package com.assignpro.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.assignpro.backend.dto.AdminDashboardResponse;
import com.assignpro.backend.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> adminDashboard() {

        return ResponseEntity.ok(
                dashboardService.getAdminDashboard());

    }

}