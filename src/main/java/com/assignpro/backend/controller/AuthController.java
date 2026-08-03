package com.assignpro.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.assignpro.backend.dto.AuthResponse;
import com.assignpro.backend.dto.LoginRequest;
import com.assignpro.backend.dto.RegisterRequest;
import com.assignpro.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ==========================
    // REGISTER
    // ==========================
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    // ==========================
    // LOGIN
    // ==========================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // ==========================
    // TEST API
    // ==========================
    @GetMapping("/test")
    public String test() {
        return "AssignPro Backend Running Successfully";
    }
}