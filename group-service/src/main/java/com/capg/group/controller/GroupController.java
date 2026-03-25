package com.capg.group.controller;

import com.capg.group.dto.CreateGroupRequest;
import com.capg.group.entity.Group;
import com.capg.group.service.GroupService;

import java.util.List;
import com.capg.group.dto.GroupResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService service;

    public GroupController(GroupService service) {
        this.service = service;
    }

//    @PostMapping
//    public Group createGroup(@RequestBody CreateGroupRequest request, String token) {
//
//        String email = SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getName();
//
//        return service.createGroup(email, request, token);
//    }

    @PostMapping
    public Group createGroup(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateGroupRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return service.createGroup(email, request, token);
    }

    @PostMapping("/{groupId}/leave")
    public String leaveGroup(@PathVariable Long groupId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        service.leaveGroup(email, groupId);

        return "Left group successfully";
    }
    @DeleteMapping("/{groupId}/remove/{email}")
    public String removeMember(@PathVariable Long groupId,
                               @PathVariable String email) {

        String adminEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        service.removeMember(adminEmail, groupId, email);

        return "User removed successfully";
    }
    @PostMapping("/{groupId}/join")
    public String joinGroup(@PathVariable Long groupId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        service.joinGroup(email, groupId);

        return "Joined group successfully";
    }

    @GetMapping
    public List<GroupResponse> getAllGroups() {
        return service.getAllGroups();
    }

    @GetMapping("/{groupId}")
    public GroupResponse getGroup(@PathVariable Long groupId) {
        return service.getGroup(groupId);
    }
    
    @GetMapping("/my")
    public List<GroupResponse> getMyGroups() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return service.getMyGroups(email);
    }
}