package com.restro.service.impl;

import com.restro.dto.response.ApiResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restro.config.JwtUtil;
import com.restro.dto.request.LoginRequest;
import com.restro.dto.request.RegisterRequest;
import com.restro.dto.response.AuthResponse;
import com.restro.entity.Role;
import com.restro.entity.User;
import com.restro.repository.UserRepository;
import com.restro.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public ApiResponse register(RegisterRequest request) {

        if ((request.getEmail() == null || request.getEmail().isBlank()) &&
                (request.getMobileNumber() == null || request.getMobileNumber().isBlank())) {
            return new ApiResponse(400, "Either email or mobile number is required");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                return new ApiResponse(409, "Email already exists");
            }
        }

        if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()) {
            if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
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
        user.setAddress(request.getAddress());
        user.setActive(true);

        userRepository.save(user);

        return new ApiResponse(200, "User registered successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());

        return response;
    }
}
