package com.assignpro.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignpro.backend.entity.User;
import com.assignpro.backend.service.UserDashboardService;

@RestController
@RequestMapping("/api/user")
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    public UserDashboardController(UserDashboardService userDashboardService) {
        this.userDashboardService = userDashboardService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                userDashboardService.getProfile(email));
    }

}