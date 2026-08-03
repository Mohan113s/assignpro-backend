package com.assignpro.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignpro.backend.entity.Lead;
import com.assignpro.backend.entity.LeadStatus;
import com.assignpro.backend.entity.User;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    // ==========================
    // USER LEADS
    // ==========================
    List<Lead> findByAssignedUser(User user);

    List<Lead> findByAssignedUser_Email(String email);

    List<Lead> findByAssignedUserIsNull();

    // ==========================
    // STATUS
    // ==========================
    List<Lead> findByStatus(LeadStatus status);

    long countByStatus(LeadStatus status);

    // ==========================
    // COUNTS
    // ==========================
    long countByAssignedUser(User user);

    long countByAssignedUserIsNull();

    long countByAssignedUserIsNotNull();

    // ==========================
    // DUPLICATE CHECK
    // ==========================
    boolean existsByMobile(String mobile);

    // ==========================
    // SEARCH
    // ==========================
    List<Lead> findByNameContainingIgnoreCase(String name);

    List<Lead> findByMobileContaining(String mobile);

    List<Lead> findByCompanyContainingIgnoreCase(String company);

    List<Lead> findByCityContainingIgnoreCase(String city);

}