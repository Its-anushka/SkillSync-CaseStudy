package com.capg.mentor.repository;

import com.capg.mentor.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByMentorId(Long mentorId);
}
