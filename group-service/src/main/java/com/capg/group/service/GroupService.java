package com.capg.group.service;

import java.util.List;

import com.capg.group.dto.CreateGroupRequest;
import com.capg.group.dto.GroupResponse;
import com.capg.group.entity.Group;

public interface GroupService {

	Group createGroup(String email, CreateGroupRequest request, String token);
    void joinGroup(String email, Long groupId);
    GroupResponse getGroup(Long groupId);
    void leaveGroup(String email, Long groupId);
    List<GroupResponse> getMyGroups(String email);
    void removeMember(String adminEmail, Long groupId, String targetEmail);
    List<GroupResponse> getAllGroups();
}