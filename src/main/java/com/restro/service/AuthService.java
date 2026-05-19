package com.restro.service;

import com.restro.dto.request.*;
import com.restro.dto.response.ApiResponse;
import com.restro.dto.response.AuthResponse;
import com.restro.dto.response.UserResponse;
import com.restro.entity.Role;

import java.util.List;
import java.util.UUID;

public interface AuthService {

    ApiResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    ApiResponse forgotPassword(ForgotPasswordRequest request);

    ApiResponse verifyOtp(VerifyOtpRequest request);

    ApiResponse resetPassword(ResetPasswordRequest request);

    ApiResponse createRestaurantAdmin(RestaurantAdminRequest request);

    ApiResponse createDeliveryPartner(DeliveryPartnerRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID userId);

    List<UserResponse> getUsersByRole(Role role);

}
