package com.capg.mentor.client;

import com.capg.mentor.dto.SkillDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "skill-service")
public interface SkillClient {

    @GetMapping("/skills/public/{id}")
    SkillDto getSkillById(@PathVariable Long id);
}