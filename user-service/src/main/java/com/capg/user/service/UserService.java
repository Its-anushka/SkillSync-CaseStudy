package com.capg.user.service;

import java.util.List;

import com.capg.user.dto.UserDto;
import com.capg.user.dto.request.AdminUpdateProfileRequest;
import com.capg.user.dto.request.CreateProfileRequest;
import com.capg.user.dto.request.UpdateProfileRequest;
import com.capg.user.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse createProfile(String email, String role, CreateProfileRequest request);

    UserProfileResponse getMyProfile(String email);

    UserProfileResponse getUserByEmail(String email); // ADMIN use
    
    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);
    List<UserProfileResponse> getAllUsers();
    
    UserProfileResponse updateUserByAdmin(String email, AdminUpdateProfileRequest request);
    
    void deleteUser(String email);

    UserDto getUserById(Long userId);

    void updateUserRole(Long id, String role);

}