package com.capg.skill.service.Impl;

import com.capg.skill.dto.request.SkillRequest;
import com.capg.skill.dto.response.SkillResponse;
import com.capg.skill.entity.Skill;
import com.capg.skill.exceptions.BadRequestException;
import com.capg.skill.exceptions.ResourceNotFoundException;
import com.capg.skill.mapper.SkillMapper;
import com.capg.skill.repository.SkillRepository;
import com.capg.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    //Create Skill
    @Override
    public SkillResponse createSkill(SkillRequest request) {

        //Check duplicate
        skillRepository.findByName(request.getName())
                .ifPresent(skill -> {
                    throw new BadRequestException("Skill already exists");
                });

        //Convert to entity
        Skill skill = SkillMapper.toEntity(request);

        //Save
        Skill savedSkill = skillRepository.save(skill);

        //Convert to response
        return SkillMapper.toResponse(savedSkill);
    }

    //Get all skills
    @Override
    public List<SkillResponse> getAllSkills() {

        return skillRepository.findAll()
                .stream()
                .map(SkillMapper::toResponse)
                .collect(Collectors.toList());
    }

    //Get skill by ID
    @Override
    public SkillResponse getSkillById(Long id) {

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        return SkillMapper.toResponse(skill);
    }
}