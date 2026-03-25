package com.capg.mentor.repository;

import com.capg.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    long countByUserId(Long userId);

}
