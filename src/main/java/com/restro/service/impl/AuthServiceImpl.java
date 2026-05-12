package com.restro.service.impl;

import com.restro.config.SecurityUtils;
import com.restro.dto.request.*;
import com.restro.dto.response.ApiResponse;
import com.restro.entity.*;
import com.restro.repository.RestaurantRepository;
import com.restro.repository.UserOtpRepository;
import com.restro.service.EmailService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restro.config.JwtUtil;
import com.restro.dto.response.AuthResponse;
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
    private final RestaurantRepository restaurantRepository;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserOtpRepository userOtpRepository, EmailService emailService, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userOtpRepository = userOtpRepository;
        this.emailService = emailService;
        this.restaurantRepository = restaurantRepository;
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    @Override
    @Transactional
    public ApiResponse register(RegisterRequest request) {

        logger.info("Register request received for user: {}", request.getName());

        if ((request.getEmail() == null || request.getEmail().isBlank()) &&
                (request.getMobileNumber() == null || request.getMobileNumber().isBlank())) {

            logger.warn("Registration failed: Email and Mobile Number both are missing");
            return new ApiResponse(400, "Either email or mobile number is required");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Registration failed: Email already exists -> {}",
                        request.getEmail());

                return new ApiResponse(409, "Email already exists");
            }
        }

        if (request.getMobileNumber() != null &&
                !request.getMobileNumber().isBlank()) {

            if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
                logger.warn("Registration failed: Mobile already exists -> {}",
                        request.getMobileNumber());

                return new ApiResponse(409, "Mobile number already exists");
            }
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setGender(request.getGender());
        user.setCreatedBy(request.getName());
        user.setAlternativeMobileNumber(request.getAlternativeMobileNumber());
        user.setAddress(request.getAddress());

        user.setActive(true);
        User savedUser = userRepository.save(user);
        userRepository.flush();

        logger.info("User saved successfully with ID: {}",
                savedUser.getId());

        // 6. Send email separately
        if (savedUser.getEmail() != null && !savedUser.getEmail().isBlank()) {

            try {
                emailService.sendSimpleEmail(
                        savedUser.getEmail(),
                        "Welcome to RESTRO 🎉",
                        "Hello " + savedUser.getName() + ",\n\n" +
                                "Thank you for registering with RESTRO.\n" +
                                "Your account has been created successfully.\n\n" +
                                "Happy Ordering 🍕🍔🍟\n\n" +
                                "Regards,\nRESTRO Team");

            } catch (Exception e) {
                logger.error("Email sending failed: {}", e.getMessage());
            }
        }

        // 7. Response
        return new ApiResponse(
                200,
                "Registration successful."
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

    @Override
    public ApiResponse createRestaurantAdmin(RestaurantAdminRequest request) {
        String currentAdmin = SecurityUtils.getCurrentUser();

        // Validate admin
        if (currentAdmin == null) {
            return new ApiResponse(401, "Unauthorized");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(409, "Email already exists");
        }

        Restaurant restaurant = new Restaurant();

        restaurant.setName(request.getRestaurantName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getMobileNumber());
        restaurant.setEmail(request.getEmail());

        restaurant.setPassword(
                passwordEncoder.encode(request.getPassword()));

        restaurantRepository.save(restaurant);

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.RESTAURANT_OWNER);
        user.setRestaurant(restaurant);
        user.setCreatedBy(currentAdmin);

        userRepository.save(user);

        return new ApiResponse(
                200,
                "Restaurant owner created successfully by " + currentAdmin
        );
    }

    @Override
    public ApiResponse createDeliveryPartner(DeliveryPartnerRequest request) {

        String currentAdmin = SecurityUtils.getCurrentUser();

        if (currentAdmin == null) {
            return new ApiResponse(401, "Unauthorized");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(409, "Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.DELIVERY_PARTNER);
        user.setCreatedBy(currentAdmin); //  REAL ADMIN

        userRepository.save(user);

        return new ApiResponse(200, "Delivery Partner created by " + currentAdmin);
    }

}
