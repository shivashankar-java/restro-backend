package com.restro.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.restro.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restro.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    // Check if email already exists
    boolean existsByEmail(String email);

    // Check if mobile number already exists
    boolean existsByMobileNumber(String mobileNumber);

    // Find user by mobile number
    Optional<User> findByMobileNumber(String mobileNumber);

    List<User> findByRole(Role role);
}
