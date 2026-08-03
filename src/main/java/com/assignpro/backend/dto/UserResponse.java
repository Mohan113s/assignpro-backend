package com.assignpro.backend.dto;

public class UserResponse {

    private Long id;
    private String fullName;
    private String mobile;
    private String email;
    private String role;
    private Boolean enabled;
    private long assignedLeads;

    public UserResponse() {
    }

    public UserResponse(Long id, String fullName, String mobile,
                        String email, String role,
                        Boolean enabled, long assignedLeads) {

        this.id = id;
        this.fullName = fullName;
        this.mobile = mobile;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.assignedLeads = assignedLeads;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public long getAssignedLeads() {
        return assignedLeads;
    }

    public void setAssignedLeads(long assignedLeads) {
        this.assignedLeads = assignedLeads;
    }
}