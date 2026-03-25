package com.capg.user.service;

import com.capg.user.dto.UserDto;
import com.capg.user.dto.request.CreateProfileRequest;
import com.capg.user.dto.response.UserProfileResponse;
import com.capg.user.entity.UserProfile;
import com.capg.user.exception.DuplicateResourceException;
import com.capg.user.exception.ResourceNotFoundException;
import com.capg.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserProfileRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserProfile profile;
    private CreateProfileRequest createRequest;

    @BeforeEach
    void setUp() {
        profile = new UserProfile();
        profile.setId(1L);
        profile.setEmail("test@user.com");
        profile.setRole("USER");
        profile.setFullName("Test User");
        profile.setPhone("1234567890");
        profile.setBio("A bio");
        profile.setSkills("Java, Spring");
        profile.setLocation("Location");
        profile.setActive(true);

        createRequest = new CreateProfileRequest();
        createRequest.setFullName("Test User");
        createRequest.setPhone("1234567890");
        createRequest.setBio("A bio");
        createRequest.setSkills("Java, Spring");
        createRequest.setLocation("Location");
    }

    @Test
    void createProfile_Success() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(UserProfile.class))).thenReturn(profile);

        UserProfileResponse response = userService.createProfile("test@user.com", "USER", createRequest);

        assertNotNull(response);
        assertEquals("test@user.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        
        verify(repository, times(1)).findByEmail(anyString());
        verify(repository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void createProfile_ExistingProfile_ThrowsDuplicateException() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(profile));

        assertThrows(DuplicateResourceException.class, () -> {
            userService.createProfile("test@user.com", "USER", createRequest);
        });

        verify(repository, never()).save(any(UserProfile.class));
    }

    @Test
    void getUserById_UserExists_ReturnsDto() {
        when(repository.findById(1L)).thenReturn(Optional.of(profile));

        UserDto response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test@user.com", response.getEmail());
        
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(99L);
        });
    }

    @Test
    void getUserByEmail_UserExists_ReturnsResponse() {
        when(repository.findByEmail("test@user.com")).thenReturn(Optional.of(profile));

        UserProfileResponse response = userService.getUserByEmail("test@user.com");

        assertNotNull(response);
        assertEquals("test@user.com", response.getEmail());
        
        verify(repository, times(1)).findByEmail("test@user.com");
    }
}
