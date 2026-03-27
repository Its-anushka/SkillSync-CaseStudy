package com.capg.group.controller;

import com.capg.group.dto.ApiResponse;
import com.capg.group.dto.CreateGroupRequest;
import com.capg.group.entity.Group;
import com.capg.group.entity.GroupMember;
import com.capg.group.service.GroupService;

import java.util.List;
import com.capg.group.dto.GroupResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Group Controller
 * Handles group-related REST endpoints
 * 
 * Exception Handling:
 * - ResourceNotFoundException: Thrown when group or user is not found (HTTP 404)
 * - RuntimeException: Thrown when user is already a member, not a member, or removing last admin (HTTP 400)
 * - AccessDeniedException: Thrown when user lacks required role (HTTP 403)
 */
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

    /**
     * Create a new group
     * 
     * @param request CreateGroupRequest containing group details
     * @return Group representing the created group
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public Group createGroup(@RequestBody CreateGroupRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return service.createGroup(email, request);
    }

    /**
     * Leave a group
     * 
     * @param groupId Group ID
     * @return String with success message
     * @throws RuntimeException if user is not a member or is the only admin
     */
    @PreAuthorize("hasAnyRole('USER','MENTOR')")
    @PostMapping("/{groupId}/leave")
    public String leaveGroup(@PathVariable Long groupId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        service.leaveGroup(email, groupId);

        return "Left group successfully";
    }

    /**
     * Remove a member from a group (Admin only)
     * 
     * @param groupId Group ID
     * @param email Email of the member to remove
     * @return ApiResponse with success status
     * @throws ResourceNotFoundException if admin or target user is not found
     * @throws RuntimeException if target user is not in group or trying to remove last admin
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{groupId}/remove/{email}")
    public ApiResponse<String> removeMember(@PathVariable Long groupId,
                                    @PathVariable String email) {

        String adminEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

       return service.removeMember(adminEmail, groupId, email);
    }
    /**
     * Join an existing group
     * 
     * @param groupId Group ID
     * @return String with success message
     * @throws RuntimeException if group not found or user is already a member
     */
    @PreAuthorize("hasAnyRole('USER','MENTOR')")
    @PostMapping("/{groupId}/join")
    public String joinGroup(@PathVariable Long groupId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        service.joinGroup(email, groupId);

        return "Joined group successfully";
    }

    /**
     * Retrieve all groups
     * 
     * @return List of GroupResponse objects
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN', 'MENTOR')")
    @GetMapping
    public List<GroupResponse> getAllGroups() {
        return service.getAllGroups();
    }

    /**
     * Retrieve group by ID
     * 
     * @param groupId Group ID
     * @return GroupResponse for the given ID
     * @throws ResourceNotFoundException if group is not found
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN', 'MENTOR')")
    @GetMapping("/{groupId}")
    public GroupResponse getGroup(@PathVariable Long groupId) {
        return service.getGroup(groupId);
    }


    /**
     * Retrieve groups belonging to the authenticated user
     * 
     * @return List of GroupResponse objects for the user
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public List<GroupResponse> getMyGroups() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return service.getMyGroups(email);
    }

    /**
     * Retrieve members of a group
     * 
     * @param groupId Group ID
     * @return List of GroupMember objects
     * @throws RuntimeException if group is not found
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN', 'MENTOR')")
    @GetMapping("/{groupId}/members")
    public List<GroupMember> getGroupMembers(@PathVariable Long groupId){
        return service.getGroupMembers(groupId);
    }


}