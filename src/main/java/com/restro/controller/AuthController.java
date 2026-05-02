package com.restro.controller;

import com.restro.dto.request.*;
import com.restro.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restro.dto.response.AuthResponse;
import com.restro.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
		super();
		this.authService = authService;
	}

    //  Register API
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        ApiResponse response = authService.register(request);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    //  Login API
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    // Forgot Password - Send OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        ApiResponse response = authService.forgotPassword(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {

        ApiResponse response = authService.verifyOtp(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {

        ApiResponse response = authService.resetPassword(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

}