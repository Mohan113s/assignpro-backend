package com.assignpro.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.assignpro.backend.dto.NoteRequest;
import com.assignpro.backend.entity.Lead;
import com.assignpro.backend.entity.LeadNote;
import com.assignpro.backend.entity.User;
import com.assignpro.backend.repository.LeadNoteRepository;
import com.assignpro.backend.repository.LeadRepository;
import com.assignpro.backend.repository.UserRepository;

@Service
public class LeadNoteService {

    private final LeadNoteRepository leadNoteRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    public LeadNoteService(
            LeadNoteRepository leadNoteRepository,
            LeadRepository leadRepository,
            UserRepository userRepository) {

        this.leadNoteRepository = leadNoteRepository;
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    // ==========================
    // ADD NOTE
    // ==========================
    public LeadNote addNote(NoteRequest request, String email) {

        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeadNote note = new LeadNote();

        note.setLead(lead);
        note.setUser(user);
        note.setNote(request.getNote());

        return leadNoteRepository.save(note);
    }

    // ==========================
    // GET NOTES
    // ==========================
    public List<LeadNote> getLeadNotes(Long leadId) {

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        return leadNoteRepository.findByLeadOrderByCreatedAtDesc(lead);
    }

}