package com.restro.service;

import com.restro.dto.request.*;
import com.restro.dto.response.ApiResponse;
import com.restro.dto.response.AuthResponse;

public interface AuthService {

    ApiResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    ApiResponse forgotPassword(ForgotPasswordRequest request);

    ApiResponse verifyOtp(VerifyOtpRequest request);

    ApiResponse resetPassword(ResetPasswordRequest request);

}
