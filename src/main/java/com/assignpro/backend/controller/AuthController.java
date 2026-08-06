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
    // GET ME
    // ==========================
    @GetMapping("/me")
    public ResponseEntity<com.assignpro.backend.dto.UserResponse> getMe(@RequestHeader("Authorization") String token) {
        // Just extract email from token and load user
        String email = authService.getEmailFromToken(token.substring(7));
        return ResponseEntity.ok(authService.getUserProfile(email));
    }

    // ==========================
    // TEST API
    // ==========================
    @GetMapping("/test")
    public String test() {
        return "AssignPro Backend Running Successfully";
    }

    // ==========================
    // VERIFY EMAIL
    // ==========================
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    // ==========================
    // FORGOT PASSWORD
    // ==========================
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    // ==========================
    // RESET PASSWORD
    // ==========================
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return ResponseEntity.ok(authService.resetPassword(token, newPassword));
    }
}