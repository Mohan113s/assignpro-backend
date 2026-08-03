package com.assignpro.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.assignpro.backend.dto.BulkAssignRequest;
import com.assignpro.backend.dto.LeadImportResponse;
import com.assignpro.backend.dto.ManualAssignRequest;
import com.assignpro.backend.entity.Lead;
import com.assignpro.backend.entity.LeadStatus;
import com.assignpro.backend.service.LeadService;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    // ==========================
    // CREATE LEAD
    // ==========================
    @PostMapping
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        return ResponseEntity.ok(leadService.saveLead(lead));
    }

    // ==========================
    // GET ALL LEADS
    // ==========================
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    // ==========================
    // GET UNASSIGNED LEADS
    // ==========================
    @GetMapping("/unassigned")
    public ResponseEntity<List<Lead>> getUnassignedLeads() {
        return ResponseEntity.ok(leadService.getUnassignedLeads());
    }

    // ==========================
    // GET LEAD BY ID
    // ==========================
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    // ==========================
    // DELETE LEAD
    // ==========================
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<String> deleteLead(@PathVariable Long id) {

        leadService.deleteLead(id);

        return ResponseEntity.ok("Lead Deleted Successfully");
    }

    // ==========================
    // ASSIGN SINGLE LEAD
    // ==========================
    @PutMapping("/{leadId:\\d+}/assign/{userId:\\d+}")
    public ResponseEntity<Lead> assignLead(
            @PathVariable Long leadId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                leadService.assignLead(leadId, userId));
    }

    // ==========================
    // BULK ASSIGN LEADS
    // ==========================
    @PutMapping("/assign-bulk")
    public ResponseEntity<String> bulkAssignLeads(
            @RequestBody BulkAssignRequest request) {

        return ResponseEntity.ok(
                leadService.bulkAssignLeads(request));
    }
    
 // ==========================
 // SEARCH BY NAME
 // ==========================
 @GetMapping("/search/name")
 public ResponseEntity<List<Lead>> searchByName(
         @RequestParam String name) {

     return ResponseEntity.ok(
             leadService.searchByName(name));
 }

 // ==========================
 // SEARCH BY MOBILE
 // ==========================
 @GetMapping("/search/mobile")
 public ResponseEntity<List<Lead>> searchByMobile(
         @RequestParam String mobile) {

     return ResponseEntity.ok(
             leadService.searchByMobile(mobile));
 }

 // ==========================
 // SEARCH BY COMPANY
 // ==========================
 @GetMapping("/search/company")
 public ResponseEntity<List<Lead>> searchByCompany(
         @RequestParam String company) {

     return ResponseEntity.ok(
             leadService.searchByCompany(company));
 }

 // ==========================
 // SEARCH BY CITY
 // ==========================
 @GetMapping("/search/city")
 public ResponseEntity<List<Lead>> searchByCity(
         @RequestParam String city) {

     return ResponseEntity.ok(
             leadService.searchByCity(city));
 }

 // ==========================
 // FILTER BY STATUS
 // ==========================
 @GetMapping("/status")
 public ResponseEntity<List<Lead>> getLeadsByStatus(
         @RequestParam LeadStatus status) {

     return ResponseEntity.ok(
             leadService.getLeadsByStatus(status));
 }

    // ==========================
    // MANUAL LEAD DISTRIBUTION
    // ==========================
    @PutMapping("/manual-assign")
    public ResponseEntity<String> manualAssign(
            @RequestBody ManualAssignRequest request) {

        return ResponseEntity.ok(
                leadService.manualAssignLeads(request));
    }

    // ==========================
    // GET USER LEADS (ADMIN)
    // ==========================
    @GetMapping("/user/{userId:\\d+}")
    public ResponseEntity<List<Lead>> getUserLeads(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                leadService.getMyLeads(userId));
    }

    // ==========================
    // MY LEADS (LOGGED-IN USER)
    // ==========================
    @GetMapping("/my-leads")
    public ResponseEntity<List<Lead>> getMyLeads(
            Authentication authentication) {

        return ResponseEntity.ok(
                leadService.getMyLeads(authentication.getName()));
    }

    // ==========================
    // UPDATE NOTES
    // ==========================
    @PutMapping("/{leadId:\\d+}/notes")
    public ResponseEntity<Lead> updateNotes(
            @PathVariable Long leadId,
            @RequestParam String notes) {

        return ResponseEntity.ok(
                leadService.updateNotes(leadId, notes));
    }

    // ==========================
    // UPDATE STATUS
    // ==========================
    @PutMapping("/{leadId:\\d+}/status")
    public ResponseEntity<Lead> updateStatus(
            @PathVariable Long leadId,
            @RequestParam LeadStatus status) {

        return ResponseEntity.ok(
                leadService.updateLeadStatus(leadId, status));
    }

    // ==========================
    // IMPORT EXCEL
    // ==========================
    @PostMapping("/import")
    public ResponseEntity<LeadImportResponse> importExcel(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                leadService.importExcel(file));
    }

}