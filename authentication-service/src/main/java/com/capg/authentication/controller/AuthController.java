package com.capg.authentication.controller;

import com.capg.authentication.dto.AuthResponse;
import com.capg.authentication.dto.LoginRequest;
import com.capg.authentication.dto.RegisterRequest;
import com.capg.authentication.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Constructor Injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register API
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return authService.refresh(token);
    }
    
    //test
    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin access only";
    }

    @GetMapping("/user/test")
    public String userTest() {
        return "User access";
    }
}