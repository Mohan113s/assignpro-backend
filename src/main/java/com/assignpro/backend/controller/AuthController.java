package com.assignpro.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.assignpro.backend.dto.AuthResponse;
import com.assignpro.backend.dto.LoginRequest;
import com.assignpro.backend.dto.RegisterRequest;
import com.assignpro.backend.dto.UserResponse;
import com.assignpro.backend.service.AuthService;

/**
 * AuthController — public auth endpoints.
 *
 * POST /api/auth/register — create account (returns JWT immediately)
 * POST /api/auth/login — sign in (returns JWT)
 * GET /api/auth/me — get current user (requires JWT)
 * GET /api/auth/test — health check (public)
 * GET /api/auth/verify — email verification (public)
 * POST /api/auth/logout — client-side logout hint (stateless JWT, no server
 * state)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = { "https://*.github.io", "http://localhost:*", "http://127.0.0.1:*" })
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // GET /api/auth/me — requires valid JWT (handled by JwtAuthenticationFilter)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        // Get email from Spring Security context (populated by JwtAuthenticationFilter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        return ResponseEntity.ok(authService.getUserProfile(email));
    }

    // POST /api/auth/logout — stateless JWT, so just acknowledge (token cleared
    // client-side)
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }

    // GET /api/auth/test — health check
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("AssignPro Backend Running Successfully ✓");
    }

    // GET /api/auth/verify — email verification
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    // POST /api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    // POST /api/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(authService.resetPassword(token, newPassword));
    }
}