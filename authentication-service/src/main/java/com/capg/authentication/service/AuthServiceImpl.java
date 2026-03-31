package com.capg.authentication.service;

import com.capg.authentication.client.UserClient;
import com.capg.authentication.dto.AuthResponse;
import com.capg.authentication.dto.LoginRequest;
import com.capg.authentication.dto.RegisterRequest;
import com.capg.authentication.dto.UserProfileRequest;
import com.capg.authentication.entity.User;
import com.capg.authentication.exception.UserAlreadyExistsException;
import com.capg.authentication.repository.UserRepository;
import com.capg.authentication.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserClient userClient; // 🔥 Feign
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    // Constructor Injection
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserClient userClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userClient = userClient;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        //Check duplicate
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with this email");
        }

        //Create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Generate JWT immediately
        String token = JwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        //Call user-service (Feign)
        try {
            UserProfileRequest profile = new UserProfileRequest();
            profile.setFullName(savedUser.getName());
            profile.setPhone("");
            profile.setLocation("");
            profile.setBio("");
            profile.setSkills("");

            userClient.createProfile("Bearer " + token, profile);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return new AuthResponse("User registered successfully", token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = JwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse("Login successful", token);
    }

    @Override
    public AuthResponse refresh(String token) {
        if (!JwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String email = JwtUtil.extractEmail(token);
        String role = JwtUtil.extractRole(token);

        String newToken = JwtUtil.generateToken(email, role);

        return new AuthResponse("Token refreshed successfully", newToken);
    }
}