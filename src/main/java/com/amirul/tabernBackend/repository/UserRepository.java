package com.amirul.tabernBackend.repository;

import com.amirul.tabernBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Basic findByEmail method
    Optional<User> findByEmail(String email);

    // Case-insensitive email search
    Optional<User> findByEmailIgnoreCase(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if email exists (case-insensitive)
    boolean existsByEmailIgnoreCase(String email);

    // Custom query with JPQL
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailCaseInsensitive(@Param("email") String email);

    // Find user with email and enabled status
    Optional<User> findByEmailAndEnabledTrue(String email);

    // Find user by email with roles eager loaded
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    Optional<User> findByUsername(String username);
}
