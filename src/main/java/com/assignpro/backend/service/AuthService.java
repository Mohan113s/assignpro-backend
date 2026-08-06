package com.assignpro.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.assignpro.backend.dto.AuthResponse;
import com.assignpro.backend.dto.LoginRequest;
import com.assignpro.backend.dto.RegisterRequest;
import com.assignpro.backend.dto.UserResponse;
import com.assignpro.backend.entity.Role;
import com.assignpro.backend.entity.User;
import com.assignpro.backend.repository.UserRepository;
import com.assignpro.backend.security.JwtService;

/**
 * AuthService — handles registration and login.
 *
 * REGISTER: saves user to AWS PostgreSQL → generates JWT immediately →
 * returns AuthResponse with token + user object.
 * Email sending is attempted but NEVER blocks registration.
 *
 * LOGIN: authenticates against AWS PostgreSQL → generates JWT → returns
 * AuthResponse.
 *
 * No local storage. No mock auth. No demo mode.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    // Admin security key — must match Flutter's _kAdminSecurityKey constant
    private static final String ADMIN_SECURITY_KEY = "707586";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    // ==========================
    // REGISTER
    // Returns: JWT token + user object immediately (no email verification required)
    // Email failure NEVER blocks registration.
    // ==========================
    public AuthResponse register(RegisterRequest request) {

        // Validate duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            AuthResponse error = new AuthResponse();
            error.setMessage("Email already exists. Please use a different email or sign in.");
            return error;
        }

        // Validate duplicate mobile
        if (request.getMobile() != null && !request.getMobile().isBlank()
                && userRepository.existsByMobile(request.getMobile())) {
            AuthResponse error = new AuthResponse();
            error.setMessage("Mobile number already registered.");
            return error;
        }

        // Determine role
        Role role;
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            if (!ADMIN_SECURITY_KEY.equals(request.getSecurityKey())) {
                AuthResponse error = new AuthResponse();
                error.setMessage("Invalid Security Key. Admin access denied.");
                return error;
            }
            role = Role.ADMIN;
        } else {
            role = Role.USER;
        }

        // Create and save user to AWS PostgreSQL
        User user = new User();
        user.setFullName(request.getFullName());
        user.setMobile(request.getMobile() != null ? request.getMobile() : "");
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEnabled(true);
        user.setIsVerified(true); // Auto-verified — no email gate for production login
        user.setVerificationToken(java.util.UUID.randomUUID().toString());

        user = userRepository.save(user);
        log.info("New {} registered: {}", role.name(), user.getEmail());

        // Generate JWT immediately so Flutter can log the user in right away
        String token = jwtService.generateToken(user.getEmail());

        // Attempt to send verification email — MUST NOT block or fail registration
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
        } catch (Exception e) {
            log.warn("Verification email failed for {} — registration still succeeded: {}", user.getEmail(),
                    e.getMessage());
        }

        UserResponse userResponse = toUserResponse(user);

        AuthResponse response = new AuthResponse(token, role.name(), "Registration Successful", userResponse);
        return response;
    }

    // ==========================
    // VERIFY EMAIL
    // ==========================
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification token"));

        user.setIsVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Email verified successfully! You can now log in.";
    }

    // ==========================
    // FORGOT PASSWORD
    // ==========================
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with that email"));

        user.setResetToken(java.util.UUID.randomUUID().toString());
        userRepository.save(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getResetToken());
        } catch (Exception e) {
            log.warn("Password reset email failed for {}: {}", email, e.getMessage());
        }

        return "If the email exists, a password reset link has been sent.";
    }

    // ==========================
    // RESET PASSWORD
    // ==========================
    public String resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        return "Password reset successfully. You can now log in.";
    }

    // ==========================
    // LOGIN
    // Reads user from AWS PostgreSQL, validates password, returns JWT + user
    // ==========================
    public AuthResponse login(LoginRequest request) {

        // Spring Security validates credentials against AWS PostgreSQL via
        // CustomUserDetailsService
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getRole().name(),
                "Login Successful",
                toUserResponse(user));
    }

    // ==========================
    // GET PROFILE (from JWT)
    // ==========================
    public String getEmailFromToken(String token) {
        return jwtService.extractUsername(token);
    }

    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toUserResponse(user);
    }

    // ==========================
    // HELPERS
    // ==========================
    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getMobile(),
                user.getEmail(),
                user.getRole().name(),
                user.getEnabled(),
                0L);
    }
}