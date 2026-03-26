package com.capg.group.service;

import com.capg.group.dto.ApiResponse;
import com.capg.group.dto.CreateGroupRequest;
import com.capg.group.dto.GroupResponse;
import com.capg.group.dto.MemberDto;
import com.capg.group.entity.Group;
import com.capg.group.entity.GroupMember;
import com.capg.group.exception.ResourceNotFoundException;
import com.capg.group.repository.GroupMemberRepository;
import com.capg.group.repository.GroupRepository;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;

    public GroupServiceImpl(GroupRepository groupRepository,
            GroupMemberRepository memberRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Group createGroup(String email, CreateGroupRequest request) {

        // email is already validated by JWT — no user-service call needed
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(email);

        Group savedGroup = groupRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroupId(savedGroup.getId());
        member.setUserEmail(email);
        member.setRole("ADMIN");

        memberRepository.save(member);

        return savedGroup;
    }

	@Override
	public void joinGroup(String email, Long groupId) {
		 // 1. Check group exists
	    Group group = groupRepository.findById(groupId)
	            .orElseThrow(() -> new RuntimeException("Group not found"));

	    // 2. Check already joined
	    if (memberRepository.findByGroupIdAndUserEmail(groupId, email).isPresent()) {
	        throw new RuntimeException("Already a member");
	    }

	    // 3. Add member
	    GroupMember member = new GroupMember();
	    member.setGroupId(groupId);
	    member.setUserEmail(email);
	    member.setRole("MEMBER");

	    memberRepository.save(member);
		
	}
	@Override
	public GroupResponse getGroup(Long groupId) {

		Group group = groupRepository.findById(groupId)
	            .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

	    var members = memberRepository.findByGroupId(groupId)
	            .stream()
	            .map(m -> new MemberDto(m.getUserEmail(), m.getRole()))
	            .toList();

	    GroupResponse response = new GroupResponse();
	    response.setId(group.getId());
	    response.setName(group.getName());
	    response.setDescription(group.getDescription());
	    response.setCreatedBy(group.getCreatedBy());
	    response.setMembers(members);

	    return response;
	}

	@Override
	public void leaveGroup(String email, Long groupId) {
		 GroupMember member = memberRepository
		            .findByGroupIdAndUserEmail(groupId, email).orElseThrow(() -> new RuntimeException("Not a member of this group"));

		    // 🔥 If ADMIN → check if last admin
		    if ("ADMIN".equals(member.getRole())) {

		        long adminCount = memberRepository
		                .countByGroupIdAndRole(groupId, "ADMIN");

		        if (adminCount == 1) {
		            throw new RuntimeException("Cannot leave as the only admin");
		        }
		    }

		    memberRepository.delete(member);
		
	}

	@Override
	public ApiResponse<String> removeMember(String adminEmail, Long groupId, String targetEmail) {

		    // 1. Check admin
		    GroupMember admin = memberRepository
		            .findByGroupIdAndUserEmail(groupId, adminEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		    if (!"ADMIN".equals(admin.getRole())) {
				return ApiResponse.<String>builder()
						.success(true)
						.message("Only admin can remove members")
						.data(null)
						.build();
		    }

		    // 2. Find target
		    GroupMember target = memberRepository
		            .findByGroupIdAndUserEmail(groupId, targetEmail).orElseThrow(()-> new RuntimeException("User not in group"));

		    // 3. Prevent removing last admin
		    if ("ADMIN".equals(target.getRole())) {
		        long adminCount = memberRepository
		                .countByGroupIdAndRole(groupId, "ADMIN");

		        if (adminCount == 1) {
					return ApiResponse.<String>builder()
							.success(true)
							.message("Cannot remove last admin")
							.data(null)
							.build();
		        }
		    }

		    memberRepository.delete(target);
			return ApiResponse.<String>builder()
				.success(true)
				.message("Member Removed Successfully")
				.data(null)
				.build();
		}
		
	@Override
	public List<GroupResponse> getMyGroups(String email) {

	    return memberRepository.findByUserEmail(email)
	            .stream()
	            .map(member -> {

	                Group group = groupRepository.findById(member.getGroupId())
	                        .orElseThrow(() -> new RuntimeException("Group not found"));

	                GroupResponse response = new GroupResponse();
	                response.setId(group.getId());
	                response.setName(group.getName());
	                response.setDescription(group.getDescription());
	                response.setCreatedBy(group.getCreatedBy());

	                return response;

	            })
	            .toList();
	}

	@Override
	public List<GroupResponse> getAllGroups() {
	    return groupRepository.findAll()
	            .stream()
	            .map(group -> {
	                GroupResponse response = new GroupResponse();
	                response.setId(group.getId());
	                response.setName(group.getName());
	                response.setDescription(group.getDescription());
	                response.setCreatedBy(group.getCreatedBy());
	                return response;
	            })
	            .toList();
	}

	@Override
	public List<GroupMember> getGroupMembers(Long groupId) {
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
		List<GroupMember> members = memberRepository.findByGroupId(group.getId()).stream().toList();
		return members;
	}

}
