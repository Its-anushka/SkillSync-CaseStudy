package com.capg.skill.service;

import com.capg.skill.dto.request.SkillRequest;
import com.capg.skill.dto.response.SkillResponse;

import java.util.List;

public interface SkillService {

    SkillResponse createSkill(SkillRequest request);

    List<SkillResponse> getAllSkills();

    SkillResponse getSkillById(Long id);
}