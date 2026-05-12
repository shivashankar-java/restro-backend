package com.restro.repository;

import com.restro.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserOtpRepository extends JpaRepository<UserOtp, UUID> {

    Optional<UserOtp> findTopByUsernameOrderByIdDesc(String username);

}
