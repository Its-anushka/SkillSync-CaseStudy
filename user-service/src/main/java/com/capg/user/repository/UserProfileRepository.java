package com.capg.user.repository;

import com.capg.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    List<UserProfile> findAll();
    Optional<UserProfile> findByEmail(String email);
    void deleteByEmail(String email);

 
}