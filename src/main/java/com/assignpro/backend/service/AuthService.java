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

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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

        userRepository.save(user);

        return new AuthResponse(
                null,
                role.name(),
                "Registration Successful");
    }

    // ==========================
    // LOGIN
    // ==========================
    public AuthResponse login(LoginRequest request) {

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
                "Login Successful");
    }

}