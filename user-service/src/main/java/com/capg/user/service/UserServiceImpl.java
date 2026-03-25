package com.capg.user.service;

import com.capg.user.dto.UserDto;
import com.capg.user.dto.*;
import com.capg.user.dto.request.AdminUpdateProfileRequest;
import com.capg.user.dto.request.CreateProfileRequest;
import com.capg.user.dto.request.UpdateProfileRequest;
import com.capg.user.dto.response.UserProfileResponse;
import com.capg.user.entity.UserProfile;
import com.capg.user.exception.DuplicateResourceException;
import com.capg.user.exception.ResourceNotFoundException;
import com.capg.user.repository.UserProfileRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserProfileRepository repository;

    public UserServiceImpl(UserProfileRepository repository) {
        this.repository = repository;
    }

    // 🔥 CREATE PROFILE
    @Override
    public UserProfileResponse createProfile(String email, String role, CreateProfileRequest request) {

        if (repository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Profile already exists");
        }

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setRole(role);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setBio(request.getBio());
        profile.setSkills(request.getSkills());
        profile.setLocation(request.getLocation());
        profile.setActive(true);

        return mapToResponse(repository.save(profile));
    }

    // 🔥 GET MY PROFILE
    @Override
    public UserProfileResponse getMyProfile(String email) {

        UserProfile profile = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return mapToResponse(profile);
    }

    // 🔥 ADMIN GET USER
    @Override
    public UserProfileResponse getUserByEmail(String email) {

        UserProfile profile = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToResponse(profile);
    }

    // 🔥 UPDATE OWN PROFILE
    @Override
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {

        UserProfile profile = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        updateFields(profile, request);

        return mapToResponse(repository.save(profile));
    }

    // 🔥 ADMIN UPDATE ANY USER
    @Override
    public UserProfileResponse updateUserByAdmin(String email, AdminUpdateProfileRequest request) {

        UserProfile profile = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        updateFields(profile, request);

        return mapToResponse(repository.save(profile));
    }

    // 🔥 DELETE USER (ADMIN)
    @Override
    public void deleteUser(String email) {

        UserProfile profile = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        repository.delete(profile);
    }

    @Override
    public UserDto getUserById(Long userId) {
        UserProfile user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());

        return dto;
    }

    @Override
    public void updateUserRole(Long id, String role) {
        UserProfile user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(role);
        repository.save(user);
    }

    // 🔥 GET ALL USERS (ADMIN)
    @Override
    public List<UserProfileResponse> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🔥 COMMON UPDATE LOGIC (CLEAN CODE)
    private void updateFields(UserProfile profile, AdminUpdateProfileRequest request) {
       if (request.getRole() != null) profile.setRole(request.getRole());
    }

    private void updateFields(UserProfile profile, UpdateProfileRequest request) {
        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
    }

    // 🔥 MAPPER
    private UserProfileResponse mapToResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getEmail(),
                profile.getFullName(),
                profile.getRole(),
                profile.getPhone(),
                profile.getBio(),
                profile.getSkills(),
                profile.getLocation()
        );
    }
}