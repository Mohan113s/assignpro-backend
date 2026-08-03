package com.assignpro.backend.dto;

public class AuthResponse {

    private String token;
    private String role;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, String role, String message) {
        this.token = token;
        this.role = role;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}