package com.capg.user.dto.request;

import jakarta.validation.constraints.NotNull;

public class AdminUpdateProfileRequest {

    @NotNull
    private String role;

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

}