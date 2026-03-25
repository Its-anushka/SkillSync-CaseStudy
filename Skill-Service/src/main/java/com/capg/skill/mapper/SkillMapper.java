package com.capg.skill.mapper;

import com.capg.skill.dto.request.SkillRequest;
import com.capg.skill.dto.response.SkillResponse;
import com.capg.skill.entity.Skill;

public class SkillMapper {

    //Request → Entity
    public static Skill toEntity(SkillRequest request) {
        return Skill.builder()
                .name(request.getName())
                .category(request.getCategory())
                .build();
    }

    //Entity → Response
    public static SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .build();
    }
}
