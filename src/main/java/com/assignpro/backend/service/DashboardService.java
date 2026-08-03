package com.assignpro.backend.service;

import org.springframework.stereotype.Service;

import com.assignpro.backend.dto.AdminDashboardResponse;
import com.assignpro.backend.repository.LeadRepository;
import com.assignpro.backend.repository.UserRepository;

@Service
public class DashboardService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    public DashboardService(LeadRepository leadRepository,
                            UserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    public AdminDashboardResponse getAdminDashboard() {

        long totalLeads = leadRepository.count();

        long assignedLeads = leadRepository.countByAssignedUserIsNotNull();

        long unassignedLeads = leadRepository.countByAssignedUserIsNull();

        long totalUsers = userRepository.count();

        return new AdminDashboardResponse(
                totalLeads,
                assignedLeads,
                unassignedLeads,
                totalUsers
        );
    }
}