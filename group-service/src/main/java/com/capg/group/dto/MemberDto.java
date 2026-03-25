package com.capg.group.dto;

public class MemberDto {

    private String email;
    private String role;

    public MemberDto(String email, String role) {
        this.email = email;
        this.role = role;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    	
}