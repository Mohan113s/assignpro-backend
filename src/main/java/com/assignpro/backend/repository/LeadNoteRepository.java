package com.assignpro.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignpro.backend.entity.Lead;
import com.assignpro.backend.entity.LeadNote;

public interface LeadNoteRepository extends JpaRepository<LeadNote, Long> {

    List<LeadNote> findByLeadOrderByCreatedAtDesc(Lead lead);

}