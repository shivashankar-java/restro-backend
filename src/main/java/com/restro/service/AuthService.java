package com.restro.service;

import com.restro.dto.request.LoginRequest;
import com.restro.dto.request.RegisterRequest;
import com.restro.dto.response.ApiResponse;
import com.restro.dto.response.AuthResponse;

public interface AuthService {

    ApiResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}
