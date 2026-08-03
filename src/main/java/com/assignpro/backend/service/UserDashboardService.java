package com.assignpro.backend.service;

import org.springframework.stereotype.Service;

import com.assignpro.backend.entity.User;
import com.assignpro.backend.repository.UserRepository;

@Service
public class UserDashboardService {

    private final UserRepository userRepository;

    public UserDashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getProfile(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

}