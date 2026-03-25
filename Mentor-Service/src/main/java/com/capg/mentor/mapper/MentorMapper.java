package com.capg.mentor.mapper;

import com.capg.mentor.client.SkillClient;
import com.capg.mentor.dto.request.MentorRequest;
import com.capg.mentor.dto.response.ApprovedMentorResponse;
import com.capg.mentor.dto.response.MentorResponse;
import com.capg.mentor.entity.Mentor;
import com.capg.mentor.entity.MentorSkill;
import com.capg.mentor.enums.MentorStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MentorMapper {

    // Convert Apply Request → Mentor Entity
    public static Mentor toEntity(MentorRequest request) {
        return Mentor.builder()
                .userId(request.getUserId())
                .bio(request.getBio())
                .experience(request.getExperience())
                .hourlyRate(request.getHourlyRate())
                .rating(0.0) // default
                .status(MentorStatus.PENDING)
                .build();
    }

    // Convert skillIds → MentorSkill entities
    public static List<MentorSkill> toMentorSkills(Long mentorId, List<Long> skillIds) {
        return skillIds.stream()
                .map(skillId -> MentorSkill.builder()
                        .mentorId(mentorId)
                        .skillId(skillId)
                        .build())
                .collect(Collectors.toList());
    }

    //Convert Entity → Response DTO
    public static MentorResponse toResponse(Mentor mentor, List<Long> skillIds, SkillClient client) {
        List<String> skill = skillIds.stream().map(id -> client.getSkillById(id).getName()).toList();
        return MentorResponse.builder()
                .id(mentor.getMentorId())
                .userId(mentor.getUserId())
                .bio(mentor.getBio())
                .experience(mentor.getExperience())
                .rating(mentor.getRating())
                .hourlyRate(mentor.getHourlyRate())
                .skillId(skillIds)
                .skills(skill)
                .build();
    }

    //Convert Entity -> Approved Mentor
    public static ApprovedMentorResponse toApprovedResponse(Mentor mentor) {

        return ApprovedMentorResponse.builder()
                .mentorId(mentor.getMentorId())
                .userId(mentor.getUserId())
                .bio(mentor.getBio())
                .experience(mentor.getExperience())
                .hourlyRate(mentor.getHourlyRate())
                .build();
    }

}