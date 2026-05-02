package com.restro.repository;

import com.restro.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

    Optional<UserOtp> findTopByUsernameOrderByIdDesc(String username);

}
