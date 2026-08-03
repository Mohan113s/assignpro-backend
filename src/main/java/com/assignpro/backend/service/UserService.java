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

    public UserService(UserRepository userRepository,
                       LeadRepository leadRepository) {

        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
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
}