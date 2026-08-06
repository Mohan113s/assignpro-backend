package com.assignpro.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.assignpro.backend.dto.UserResponse;
import com.assignpro.backend.entity.User;
import com.assignpro.backend.repository.LeadRepository;
import com.assignpro.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            LeadRepository leadRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(

                        user.getId(),
                        user.getFullName(),
                        user.getMobile(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getEnabled(),
                        leadRepository.countByAssignedUser(user)

                ))
                .collect(Collectors.toList());
    }

    public com.assignpro.backend.entity.User createUser(com.assignpro.backend.dto.UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()
                && userRepository.existsByMobile(request.getPhone())) {
            throw new RuntimeException("Mobile number already exists");
        }

        com.assignpro.backend.entity.User user = new com.assignpro.backend.entity.User();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        com.assignpro.backend.entity.Role role = request.getRole() != null
                && request.getRole().equalsIgnoreCase("ADMIN")
                        ? com.assignpro.backend.entity.Role.ADMIN
                        : com.assignpro.backend.entity.Role.USER;
        user.setRole(role);

        user.setEnabled(request.getIsActive() != null ? request.getIsActive() : true);
        user.setIsVerified(true); // created by admin, treat as verified

        return userRepository.save(user);
    }

    public com.assignpro.backend.entity.User updateUser(Long id, com.assignpro.backend.dto.UserRequest request) {
        com.assignpro.backend.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty() &&
                !request.getPhone().equalsIgnoreCase(user.getMobile()) &&
                userRepository.existsByMobile(request.getPhone())) {
            throw new RuntimeException("Mobile number already exists");
        }

        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getPhone());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        com.assignpro.backend.entity.Role role = request.getRole() != null
                && request.getRole().equalsIgnoreCase("ADMIN")
                        ? com.assignpro.backend.entity.Role.ADMIN
                        : com.assignpro.backend.entity.Role.USER;
        user.setRole(role);

        if (request.getIsActive() != null) {
            user.setEnabled(request.getIsActive());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public com.assignpro.backend.entity.User toggleUserStatus(Long id, Boolean isActive) {
        com.assignpro.backend.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(isActive);
        return userRepository.save(user);
    }
}