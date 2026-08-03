package com.assignpro.backend.dto;

public class AdminDashboardResponse {

    private long totalLeads;
    private long assignedLeads;
    private long unassignedLeads;
    private long totalUsers;

    public AdminDashboardResponse(long totalLeads,
                                  long assignedLeads,
                                  long unassignedLeads,
                                  long totalUsers) {
        this.totalLeads = totalLeads;
        this.assignedLeads = assignedLeads;
        this.unassignedLeads = unassignedLeads;
        this.totalUsers = totalUsers;
    }

    public long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public long getAssignedLeads() {
        return assignedLeads;
    }

    public void setAssignedLeads(long assignedLeads) {
        this.assignedLeads = assignedLeads;
    }

    public long getUnassignedLeads() {
        return unassignedLeads;
    }

    public void setUnassignedLeads(long unassignedLeads) {
        this.unassignedLeads = unassignedLeads;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
}