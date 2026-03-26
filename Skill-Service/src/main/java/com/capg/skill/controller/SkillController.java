package com.capg.skill.controller;

import com.capg.skill.dto.request.SkillRequest;
import com.capg.skill.dto.response.SkillResponse;
import com.capg.skill.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    //Create Skill
    @PostMapping
    public SkillResponse createSkill(
            @Valid @RequestBody SkillRequest request) {

        return skillService.createSkill(request);
    }

    //Get all skills
    @GetMapping("/public")
    public List<SkillResponse> getAllSkills() {

        return skillService.getAllSkills();
    }

    //Get skill by ID
    @GetMapping("/public/{id}")
    public SkillResponse getSkillById(@PathVariable Long id) {

        return skillService.getSkillById(id);
    }
}