package com.capg.user.controller;

import com.capg.user.dto.UserDto;
;
import com.capg.user.dto.request.AdminUpdateProfileRequest;
import com.capg.user.dto.request.CreateProfileRequest;
import com.capg.user.dto.request.UpdateProfileRequest;
import com.capg.user.dto.response.UserProfileResponse;
import com.capg.user.security.JwtUtil;
import com.capg.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /*This method extracts the JWT token from the Authorization header of an HTTP request.
    It first retrieves the "Authorization" header from the request.
    If the header is missing or does not start with "Bearer ", it throws a RuntimeException.
    If valid, it removes the "Bearer " prefix and returns the actual token string.*/
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Missing token");
        }

        return header.substring(7);
    }

    /* To create user profile */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/profile")
    public UserProfileResponse createProfile(@RequestBody CreateProfileRequest request,
                                             HttpServletRequest httpRequest) {

        String token = extractToken(httpRequest);
        String email = JwtUtil.extractEmail(token);
        String role = JwtUtil.extractRole(token);

        return service.createProfile(email, role, request);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public UserProfileResponse getMyProfile(HttpServletRequest httpRequest) {

        String token = extractToken(httpRequest);
        String email = JwtUtil.extractEmail(token);

        return service.getMyProfile(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{email}")
    public UserProfileResponse getUser(@PathVariable String email) {
        return service.getUserByEmail(email);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/profile")
    public UserProfileResponse updateProfile(
            @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {

        String token = extractToken(httpRequest);
        String email = JwtUtil.extractEmail(token);

        return service.updateProfile(email, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<UserProfileResponse> getAllUsers() {
        return service.getAllUsers();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{email}")
    public UserProfileResponse updateUser(
            @PathVariable String email,
            @RequestBody AdminUpdateProfileRequest request) {

        return service.updateUserByAdmin(email, request);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{email}")
    public String deleteUser(@PathVariable String email) {

        service.deleteUser(email);
        return "User deleted successfully";
    }


    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id, HttpServletRequest request) {

        UserDto user = service.getUserById(id);
        System.out.println("AUTH HEADER: " + request.getHeader("Authorization"));

        return user;
    }

    // --- Added for HLD full compliance --- 
    @GetMapping
    public List<UserProfileResponse> getAllUsersGeneric() {
        return service.getAllUsers();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public UserProfileResponse updateUserGeneric(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {
        // Find email by id
        UserDto user = service.getUserById(id);
        return service.updateProfile(user.getEmail(), request);
    }
    // -------------------------------------

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/role")
    public ResponseEntity<Void> updateUserRole(@RequestParam Long id, @RequestParam String role){
        service.updateUserRole(id, role);
        return ResponseEntity.ok().build();
    }
}