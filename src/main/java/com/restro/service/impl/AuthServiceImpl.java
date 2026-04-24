package com.restro.service.impl;

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

	

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // ✅ FIX: take role from request
        user.setRole(
                request.getRole() != null
                        ? Role.valueOf(request.getRole().toUpperCase())
                        : Role.CUSTOMER
        );

        userRepo.save(user);
    }

	

	@Override
	public AuthResponse login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        AuthResponse res = new AuthResponse();
        res.setToken(token);
        res.setRole(user.getRole().name());
        return res;
	}
}
