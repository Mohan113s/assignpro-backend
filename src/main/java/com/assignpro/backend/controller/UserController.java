package com.assignpro.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.assignpro.backend.dto.UserResponse;
import com.assignpro.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<com.assignpro.backend.entity.User> createUser(
            @RequestBody com.assignpro.backend.dto.UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<com.assignpro.backend.entity.User> updateUser(@PathVariable Long id,
            @RequestBody com.assignpro.backend.dto.UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<com.assignpro.backend.entity.User> toggleUserStatus(@PathVariable Long id,
            @RequestBody java.util.Map<String, Boolean> body) {
        Boolean isActive = body.get("isActive");
        return ResponseEntity.ok(userService.toggleUserStatus(id, isActive));
    }
}