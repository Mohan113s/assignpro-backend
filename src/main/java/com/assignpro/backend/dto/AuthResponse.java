package com.assignpro.backend.dto;

public class AuthResponse {

    private String token;
    private String role;
    private String message;
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String token, String role, String message) {
        this.token = token;
        this.role = role;
        this.message = message;
    }

    public AuthResponse(String token, String role, String message, UserResponse user) {
        this.token = token;
        this.role = role;
        this.message = message;
        this.user = user;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
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