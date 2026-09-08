package com.assignpro.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignpro.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Case-insensitive email lookup — critical for PostgreSQL
    // PostgreSQL WHERE email = ? is case-sensitive; this generates LOWER(email) =
    // LOWER(?)
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    // Case-insensitive existence check
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobile(String mobile);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetToken(String token);

}