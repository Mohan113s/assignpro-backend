package com.assignpro.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.assignpro.backend.dto.BulkAssignRequest;
import com.assignpro.backend.dto.LeadImportResponse;
import com.assignpro.backend.dto.ManualAssignRequest;
import com.assignpro.backend.dto.UserLeadCountRequest;
import com.assignpro.backend.entity.Lead;
import com.assignpro.backend.entity.LeadStatus;
import com.assignpro.backend.entity.User;
import com.assignpro.backend.repository.LeadRepository;
import com.assignpro.backend.repository.UserRepository;
import com.assignpro.backend.util.ExcelHelper;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    public LeadService(LeadRepository leadRepository,
            UserRepository userRepository) {

        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    // ==========================
    // CREATE LEAD
    // ==========================
    public Lead saveLead(Lead lead) {
        return leadRepository.save(lead);
    }

    // ==========================
    // GET ALL LEADS
    // ==========================
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    // ==========================
    // GET LEAD BY ID
    // ==========================
    public Lead getLeadById(Long id) {

        return leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
    }

    // ==========================
    // DELETE LEAD
    // ==========================
    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }

    // ==========================
    // DELETE ALL LEADS
    // ==========================
    public void deleteAllLeads() {
        leadRepository.deleteAll();
    }

    // ==========================
    // ASSIGN SINGLE LEAD
    // ==========================
    public Lead assignLead(Long leadId, Long userId) {

        Lead lead = getLeadById(leadId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        lead.setAssignedUser(user);

        return leadRepository.save(lead);
    }

    // ==========================
    // GET USER LEADS
    // ==========================
    public List<Lead> getMyLeads(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return leadRepository.findByAssignedUser(user);
    }

    public List<Lead> getMyLeads(String email) {
        return leadRepository.findByAssignedUser_Email(email);
    }

    // ==========================
    // UPDATE NOTES
    // ==========================
    public Lead updateNotes(Long leadId, String notes) {

        Lead lead = getLeadById(leadId);

        lead.setNotes(notes);

        return leadRepository.save(lead);
    }

    // ==========================
    // UPDATE STATUS
    // ==========================
    public Lead updateLeadStatus(Long leadId, LeadStatus status) {

        Lead lead = getLeadById(leadId);

        lead.setStatus(status);

        return leadRepository.save(lead);
    }

    // ==========================
    // BULK ASSIGN
    // ==========================
    public String bulkAssignLeads(BulkAssignRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Lead> leads = leadRepository.findAllById(request.getLeadIds());

        for (Lead lead : leads) {
            lead.setAssignedUser(user);
        }

        leadRepository.saveAll(leads);

        return leads.size() + " Leads Assigned Successfully";
    }

    // ==========================
    // GET UNASSIGNED LEADS
    // ==========================
    public List<Lead> getUnassignedLeads() {
        return leadRepository.findByAssignedUserIsNull();
    }

    // ==========================
    // MANUAL ASSIGN
    // ==========================
    public String manualAssignLeads(ManualAssignRequest request) {

        List<Lead> unassignedLeads = leadRepository.findByAssignedUserIsNull();

        int currentIndex = 0;

        for (UserLeadCountRequest assignment : request.getAssignments()) {

            User user = userRepository.findById(assignment.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            int count = assignment.getLeadCount();

            for (int i = 0; i < count && currentIndex < unassignedLeads.size(); i++) {

                Lead lead = unassignedLeads.get(currentIndex);

                lead.setAssignedUser(user);

                currentIndex++;
            }
        }

        leadRepository.saveAll(unassignedLeads);

        return "Successfully assigned " + currentIndex + " leads.";
    }

    // ==========================
    // SEARCH BY NAME
    // ==========================
    public List<Lead> searchByName(String name) {
        return leadRepository.findByNameContainingIgnoreCase(name);
    }

    // ==========================
    // SEARCH BY MOBILE
    // ==========================
    public List<Lead> searchByMobile(String mobile) {
        return leadRepository.findByMobileContaining(mobile);
    }

    // ==========================
    // SEARCH BY COMPANY
    // ==========================
    public List<Lead> searchByCompany(String company) {
        return leadRepository.findByCompanyContainingIgnoreCase(company);
    }

    // ==========================
    // SEARCH BY CITY
    // ==========================
    public List<Lead> searchByCity(String city) {
        return leadRepository.findByCityContainingIgnoreCase(city);
    }

    // ==========================
    // FILTER BY STATUS
    // ==========================
    public List<Lead> getLeadsByStatus(LeadStatus status) {
        return leadRepository.findByStatus(status);
    }

    // ==========================
    // IMPORT EXCEL/CSV
    // ==========================
    public LeadImportResponse importExcel(MultipartFile file) {

        if (!ExcelHelper.hasValidFormat(file)) {
            return new LeadImportResponse(
                    0, 0, 0,
                    "Please upload a valid CSV, XLS, or XLSX file");
        }

        try {
            List<Lead> leads = ExcelHelper.fileToLeads(file);

            int total = leads.size();
            int imported = 0;
            int skipped = 0;

            for (Lead lead : leads) {
                // Duplicate Mobile Validation
                if (lead.getMobile() != null && leadRepository.existsByMobile(lead.getMobile())) {
                    skipped++;
                    continue;
                }

                leadRepository.save(lead);
                imported++;
            }

            return new LeadImportResponse(
                    total,
                    imported,
                    skipped,
                    "File Imported Successfully");

        } catch (Exception e) {
            throw new RuntimeException("Could not import file", e);
        }
    }
}