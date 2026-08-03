package com.assignpro.backend.dto;

import java.util.List;

public class BulkAssignRequest {

    private Long userId;
    private List<Long> leadIds;

    public BulkAssignRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getLeadIds() {
        return leadIds;
    }

    public void setLeadIds(List<Long> leadIds) {
        this.leadIds = leadIds;
    }
}