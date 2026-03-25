package com.capg.authentication.service;

import com.capg.authentication.client.UserClient;
import com.capg.authentication.dto.*;
import com.capg.authentication.dto.AuthResponse;
import com.capg.authentication.dto.LoginRequest;
import com.capg.authentication.dto.RegisterRequest;
import com.capg.authentication.dto.UserProfileRequest;
import com.capg.authentication.entity.User;
import com.capg.authentication.enums.Role;
import com.capg.authentication.exception.UserAlreadyExistsException;
import com.capg.authentication.repository.UserRepository;
import com.capg.authentication.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AuthServiceImpl authService;

    private User setupUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        setupUser = new User();
        setupUser.setName("John Doe");
        setupUser.setEmail("john@example.com");
        setupUser.setPassword("encoded_password");
        setupUser.setRole(Role.USER);
        setupUser.setActive(true);

        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("raw_password");
        registerRequest.setRole(Role.USER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("raw_password");
    }

    @Test
    void register_NewUser_Success() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(setupUser);

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.generateToken(anyString(), anyString())).thenReturn("mocked_jwt_token");

            AuthResponse response = authService.register(registerRequest);

            assertNotNull(response);
            assertEquals("User registered successfully", response.getMessage());
            assertEquals("mocked_jwt_token", response.getToken());

            verify(userClient, times(1)).createProfile(eq("Bearer mocked_jwt_token"), any(UserProfileRequest.class));
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Test
    void register_ExistingUser_ThrowsException() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(setupUser));

        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });

        verify(userRepository, never()).save(any(User.class));
        verify(userClient, never()).createProfile(anyString(), any(UserProfileRequest.class));
    }

    @Test
    void login_ValidCredentials_ReturnsToken() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(setupUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), setupUser.getPassword())).thenReturn(true);

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.generateToken(setupUser.getEmail(), setupUser.getRole().name()))
                    .thenReturn("mocked_jwt_token");

            AuthResponse response = authService.login(loginRequest);

            assertNotNull(response);
            assertEquals("Login successful", response.getMessage());
            assertEquals("mocked_jwt_token", response.getToken());
        }
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(setupUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), setupUser.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void refresh_ValidToken_ReturnsNewToken() {
        String oldToken = "old_valid_token";
        String newToken = "new_valid_token";

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(oldToken)).thenReturn(true);
            jwtUtilMockedStatic.when(() -> JwtUtil.extractEmail(oldToken)).thenReturn("john@example.com");
            jwtUtilMockedStatic.when(() -> JwtUtil.extractRole(oldToken)).thenReturn("USER");
            jwtUtilMockedStatic.when(() -> JwtUtil.generateToken("john@example.com", "USER")).thenReturn(newToken);

            AuthResponse response = authService.refresh(oldToken);

            assertNotNull(response);
            assertEquals("Token refreshed successfully", response.getMessage());
            assertEquals(newToken, response.getToken());
        }
    }

    @Test
    void refresh_InvalidToken_ThrowsException() {
        String invalidToken = "invalid_token";

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = mockStatic(JwtUtil.class)) {
            jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(invalidToken)).thenReturn(false);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.refresh(invalidToken);
            });

            assertEquals("Invalid or expired token", exception.getMessage());
        }
    }
}
