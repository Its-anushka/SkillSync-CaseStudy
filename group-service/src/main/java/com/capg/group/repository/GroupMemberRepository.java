package com.capg.group.repository;

import com.capg.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByGroupId(Long groupId);

    List<GroupMember> findByUserEmail(String email);

    Optional<GroupMember> findByGroupIdAndUserEmail(Long groupId, String email);

    long countByGroupIdAndRole(Long groupId, String role);
}