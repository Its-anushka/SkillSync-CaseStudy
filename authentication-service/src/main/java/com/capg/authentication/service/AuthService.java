package com.capg.authentication.service;

import com.capg.authentication.dto.AuthResponse;
import com.capg.authentication.dto.LoginRequest;
import com.capg.authentication.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String token);
}