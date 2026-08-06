package com.assignpro.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.assignpro.backend.dto.AuthResponse;
import com.assignpro.backend.dto.LoginRequest;
import com.assignpro.backend.dto.RegisterRequest;
import com.assignpro.backend.entity.Role;
import com.assignpro.backend.entity.User;
import com.assignpro.backend.repository.UserRepository;
import com.assignpro.backend.security.JwtService;

@Service
public class AuthService {

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
    // ==========================
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(null, null, "Email already exists");
        }

        if (userRepository.existsByMobile(request.getMobile())) {
            return new AuthResponse(null, null, "Mobile number already exists");
        }

        Role role;

        if ("ADMIN".equalsIgnoreCase(request.getRole())) {

            if (!ADMIN_SECURITY_KEY.equals(request.getSecurityKey())) {
                return new AuthResponse(null, null, "Invalid Security Key");
            }

            role = Role.ADMIN;

        } else {
            role = Role.USER;
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setMobile(request.getMobile());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEnabled(true);
        user.setIsVerified(false);
        user.setVerificationToken(java.util.UUID.randomUUID().toString());

        userRepository.save(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(null, role.name(),
                    "Registration successful, but failed to send verification email.");
        }

        return new AuthResponse(
                null,
                role.name(),
                "Registration Successful. Please check your email to verify your account.");
    }

    // ==========================
    // VERIFY EMAIL
    // ==========================
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setIsVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Email verified successfully! You can now login.";
    }

    // ==========================
    // FORGOT PASSWORD
    // ==========================
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setResetToken(java.util.UUID.randomUUID().toString());
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), user.getResetToken());

        return "Password reset email sent";
    }

    // ==========================
    // RESET PASSWORD
    // ==========================
    public String resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        return "Password reset successfully";
    }

    // ==========================
    // LOGIN
    // ==========================
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Bypass verification since it's just mock at the moment
        // if (!user.getIsVerified()) {
        // throw new RuntimeException("Please verify your email before logging in.");
        // }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getRole().name(),
                "Login Successful",
                new com.assignpro.backend.dto.UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getMobile(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getEnabled(),
                        0));
    }

    public String getEmailFromToken(String token) {
        return jwtService.extractUsername(token);
    }

    public com.assignpro.backend.dto.UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new com.assignpro.backend.dto.UserResponse(
                user.getId(),
                user.getFullName(),
                user.getMobile(),
                user.getEmail(),
                user.getRole().name(),
                user.getEnabled(),
                0);
    }

}