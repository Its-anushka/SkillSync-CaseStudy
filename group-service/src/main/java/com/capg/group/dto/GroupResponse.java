package com.capg.group.dto;

import java.util.List;

public class GroupResponse {

    private Long id;
    private String name;
    private String description;
    private String createdBy;

    private List<MemberDto> members;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public List<MemberDto> getMembers() {
		return members;
	}

	public void setMembers(List<MemberDto> members) {
		this.members = members;
	}


}