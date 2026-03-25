package com.capg.group.service;

import com.capg.group.dto.CreateGroupRequest;
import com.capg.group.dto.GroupResponse;
import com.capg.group.dto.MemberDto;
import com.capg.group.entity.Group;
import com.capg.group.entity.GroupMember;
import com.capg.group.repository.GroupMemberRepository;
import com.capg.group.repository.GroupRepository;
import com.capg.group.client.UserClient;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserClient userClient;
    
    public GroupServiceImpl(GroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            UserClient userClient) {
		this.groupRepository = groupRepository;
		this.memberRepository = memberRepository;
		this.userClient = userClient;
		}

    @Override
    public Group createGroup(String email, CreateGroupRequest request, String token) {

        //CALL USER SERVICE
        Object user = userClient.getMyProfile(token);

        if (user == null) {
            throw new RuntimeException("User not valid");
        }

        // normal logic
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
	    if (memberRepository.existsByGroupIdAndUserEmail(groupId, email)) {
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
	            .orElseThrow(() -> new RuntimeException("Group not found"));

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
		            .findByGroupIdAndUserEmail(groupId, email);

		    if (member == null) {
		        throw new RuntimeException("Not a member of this group");
		    }

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
	public void removeMember(String adminEmail, Long groupId, String targetEmail) {

		    // 1. Check admin
		    GroupMember admin = memberRepository
		            .findByGroupIdAndUserEmail(groupId, adminEmail);

		    if (admin == null || !"ADMIN".equals(admin.getRole())) {
		        throw new RuntimeException("Only admin can remove members");
		    }

		    // 2. Find target
		    GroupMember target = memberRepository
		            .findByGroupIdAndUserEmail(groupId, targetEmail);

		    if (target == null) {
		        throw new RuntimeException("User not in group");
		    }

		    // 3. Prevent removing last admin
		    if ("ADMIN".equals(target.getRole())) {
		        long adminCount = memberRepository
		                .countByGroupIdAndRole(groupId, "ADMIN");

		        if (adminCount == 1) {
		            throw new RuntimeException("Cannot remove last admin");
		        }
		    }

		    memberRepository.delete(target);
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
	
}
