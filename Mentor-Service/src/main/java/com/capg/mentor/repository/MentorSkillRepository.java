package com.capg.mentor.repository;

import com.capg.mentor.entity.MentorSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorSkillRepository extends JpaRepository<MentorSkill, Long> {
    public List<MentorSkill> findByMentorId(Long mentorId);
}
