package com.capg.group.service;

import java.util.List;

import com.capg.group.dto.ApiResponse;
import com.capg.group.dto.CreateGroupRequest;
import com.capg.group.dto.GroupResponse;
import com.capg.group.entity.Group;
import com.capg.group.entity.GroupMember;

public interface GroupService {

	Group createGroup(String email, CreateGroupRequest request);
    void joinGroup(String email, Long groupId);
    GroupResponse getGroup(Long groupId);
    void leaveGroup(String email, Long groupId);
    List<GroupResponse> getMyGroups(String email);
    ApiResponse<String> removeMember(String adminEmail, Long groupId, String targetEmail);
    List<GroupResponse> getAllGroups();
    List<GroupMember> getGroupMembers(Long groupId);
}