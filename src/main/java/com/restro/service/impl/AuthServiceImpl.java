package com.restro.service.impl;

import com.restro.dto.request.*;
import com.restro.dto.response.ApiResponse;
import com.restro.entity.OtpPurpose;
import com.restro.entity.UserOtp;
import com.restro.repository.UserOtpRepository;
import com.restro.service.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restro.config.JwtUtil;
import com.restro.dto.response.AuthResponse;
import com.restro.entity.Role;
import com.restro.entity.User;
import com.restro.repository.UserRepository;
import com.restro.service.AuthService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserOtpRepository userOtpRepository;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserOtpRepository userOtpRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userOtpRepository = userOtpRepository;
        this.emailService = emailService;
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    @Override
    public ApiResponse register(RegisterRequest request) {

        logger.info("Register request received for user: {}", request.getName());

        // 1. Validate email/mobile
        if ((request.getEmail() == null || request.getEmail().isBlank()) &&
                (request.getMobileNumber() == null || request.getMobileNumber().isBlank())) {

            logger.warn("Registration failed: Email and Mobile Number both are missing");
            return new ApiResponse(400, "Either email or mobile number is required");
        }

        // 2. Check email exists
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Registration failed: Email already exists -> {}", request.getEmail());
                return new ApiResponse(409, "Email already exists");
            }
        }

        // 3. Check mobile exists
        if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()) {
            if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
                logger.warn("Registration failed: Mobile already exists -> {}", request.getMobileNumber());
                return new ApiResponse(409, "Mobile number already exists");
            }
        }

        // 4. Create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setGender(request.getGender());
        user.setActive(true);

        userRepository.save(user);

        logger.info("User registered successfully: {}", request.getEmail());

        // 5. Send simple welcome email (NO OTP)
        if (user.getEmail() != null && !user.getEmail().isBlank()) {

            emailService.sendSimpleEmail(
                    user.getEmail(),
                    "Welcome to RESTRO 🎉",
                    "Hello " + user.getName() + ",\n\n" +
                            "Thank you for registering with RESTRO.\n" +
                            "Your account has been created successfully.\n\n" +
                            "We’re happy to have you with us!\n\n" +
                            "Thank you once again for choosing RESTRO 🙌\n\n" +
                            "Happy Ordering! 🍕🍔🍟\n\n" +
                            "Enjoy ordering delicious food 🍽️\n\n" +
                            "Regards,\nRESTRO Team"
            );
        }

        // 6. Response
        return new ApiResponse(
                200,
                "Registration successful. Welcome email sent."
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        logger.info("Login request received for username: {}", request.getEmail());

        User user;

        // Check whether input is Email or Mobile Number
        if (request.getEmail().contains("@")) {

            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        logger.error("Login failed: User not found with email -> {}", request.getEmail());
                        return new RuntimeException("User not found");
                    });

        } else {

            user = userRepository.findByMobileNumber(request.getEmail())
                    .orElseThrow(() -> {
                        logger.error("Login failed: User not found with mobile number -> {}", request.getEmail());
                        return new RuntimeException("User not found");
                    });
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            logger.error("Login failed: Invalid password for user -> {}", request.getEmail());

            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        logger.info("Login successful for user: {}", request.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());

        return response;
    }


    @Override
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {

        logger.info("Forgot password request received for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found for forgot password: {}", request.getEmail());
                    return new RuntimeException("User not found");
                });

        String otp = generateOtp();

        UserOtp userOtp = new UserOtp();
        userOtp.setUsername(user.getEmail());
        userOtp.setOtp(otp);
        userOtp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        userOtp.setVerified(false);

        userOtpRepository.save(userOtp);

        // Send forgot password OTP mail
        emailService.sendOtpEmail(
                user.getEmail(),
                otp,
                OtpPurpose.FORGOT_PASSWORD
        );

        logger.info("Forgot password OTP sent successfully for email: {}", request.getEmail());

        return new ApiResponse(200, "OTP sent successfully");
    }

    @Override
    public ApiResponse verifyOtp(VerifyOtpRequest request) {

        logger.info("OTP verification started for email: {}", request.getEmail());

        UserOtp userOtp = userOtpRepository
                .findTopByUsernameOrderByIdDesc(
                        request.getEmail()
                )
                .orElseThrow(() -> {
                    logger.error("OTP not found for email: {}", request.getEmail());
                    return new RuntimeException("OTP not found");
                });

        if (userOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            logger.error("OTP expired for email: {}", request.getEmail());
            return new ApiResponse(400, "OTP expired");
        }

        if (!userOtp.getOtp().equals(request.getOtp())) {
            logger.error("Invalid OTP entered for email: {}", request.getEmail());
            return new ApiResponse(400, "Invalid OTP");
        }

        userOtp.setVerified(true);
        userOtpRepository.save(userOtp);

        logger.info("OTP verified successfully for email: {}", request.getEmail());

        return new ApiResponse(200, "OTP verified successfully");
    }

    @Override
    public ApiResponse resetPassword(ResetPasswordRequest request) {

        logger.info("Reset password request received for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found: {}", request.getEmail());
                    return new RuntimeException("User not found");
                });

        UserOtp userOtp = userOtpRepository
                .findTopByUsernameOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("OTP not found for email: {}", request.getEmail());
                    return new RuntimeException("OTP not found");
                });

        // Check OTP expiry
        if (userOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            logger.error("OTP expired for email: {}", request.getEmail());
            return new ApiResponse(400, "OTP expired");
        }

        // Check OTP match
        if (!userOtp.getOtp().equals(request.getOtp())) {
            logger.error("Invalid OTP for email: {}", request.getEmail());
            return new ApiResponse(400, "Invalid OTP");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark OTP as used
        userOtp.setVerified(true);
        userOtpRepository.save(userOtp);

        // Send success mail
        emailService.sendOtpEmail(
                user.getEmail(),
                "",
                OtpPurpose.PASSWORD_RESET_SUCCESS
        );

        logger.info("Password reset successful for email: {}", request.getEmail());

        return new ApiResponse(200, "Password reset successfully");
    }


}
