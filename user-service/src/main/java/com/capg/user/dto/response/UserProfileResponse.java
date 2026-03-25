package com.capg.user.dto.response;

public class UserProfileResponse {

    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String phone;
    private String bio;
    private String skills;
    private String location;

    public UserProfileResponse() {}

    public UserProfileResponse(Long userId, String email, String fullName, String role,
                               String phone, String bio, String skills, String location) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.bio = bio;
        this.skills = skills;
        this.location = location;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getBio() { return bio; }
    public String getSkills() { return skills; }
    public String getLocation() { return location; }
}