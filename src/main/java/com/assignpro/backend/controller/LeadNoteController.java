package com.assignpro.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.assignpro.backend.dto.NoteRequest;
import com.assignpro.backend.entity.LeadNote;
import com.assignpro.backend.service.LeadNoteService;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class LeadNoteController {

    private final LeadNoteService leadNoteService;

    public LeadNoteController(LeadNoteService leadNoteService) {
        this.leadNoteService = leadNoteService;
    }

    // ==========================
    // ADD NOTE
    // ==========================
    @PostMapping
    public ResponseEntity<LeadNote> addNote(
            @RequestBody NoteRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                leadNoteService.addNote(
                        request,
                        authentication.getName()));
    }

    // ==========================
    // GET NOTES
    // ==========================
    @GetMapping("/{leadId}")
    public ResponseEntity<List<LeadNote>> getNotes(
            @PathVariable Long leadId) {

        return ResponseEntity.ok(
                leadNoteService.getLeadNotes(leadId));
    }

}