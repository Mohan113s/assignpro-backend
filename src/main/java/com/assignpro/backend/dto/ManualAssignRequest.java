package com.assignpro.backend.dto;

import java.util.List;

public class ManualAssignRequest {

    private List<UserLeadCountRequest> assignments;

    public List<UserLeadCountRequest> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<UserLeadCountRequest> assignments) {
        this.assignments = assignments;
    }
}