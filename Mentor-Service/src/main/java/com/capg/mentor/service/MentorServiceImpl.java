package com.capg.mentor.service;

import com.capg.mentor.client.SkillClient;
import com.capg.mentor.client.UserClient;
import com.capg.mentor.dto.SkillDto;
import com.capg.mentor.dto.UserDto;
import com.capg.mentor.dto.request.AvailabilityRequest;
import com.capg.mentor.dto.request.MentorRequest;
import com.capg.mentor.dto.response.ApiResponse;
import com.capg.mentor.dto.response.ApprovedMentorResponse;
import com.capg.mentor.dto.response.MentorResponse;
import com.capg.mentor.entity.Availability;
import com.capg.mentor.entity.Mentor;
import com.capg.mentor.entity.MentorSkill;
import com.capg.mentor.enums.MentorStatus;
import com.capg.mentor.exception.BadRequestException;
import com.capg.mentor.exception.ResourceNotFoundException;
import com.capg.mentor.mapper.MentorMapper;
import com.capg.mentor.repository.AvailabilityRepository;
import com.capg.mentor.repository.MentorRepository;
import com.capg.mentor.repository.MentorSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final MentorSkillRepository mentorSkillRepository;
    private final AvailabilityRepository availabilityRepository;

    private final UserClient userClient;
    private final SkillClient skillClient;

    @Override
    @Transactional
    public MentorResponse applyForMentor(MentorRequest request) {

        // 0. Validate if user has already applied
        if (mentorRepository.countByUserId(request.getUserId()) > 0) {
            throw new BadRequestException("User has already applied for mentor");
        }

        // 1. Validate user exists
        UserDto user =  userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }


        // 2. Validate skills exist
        for (Long skillId : request.getSkillIds()) {
            SkillDto skill = skillClient.getSkillById(skillId);
            if (skill == null) {
                throw new ResourceNotFoundException("Skill not found: " + skillId);
            }
        }

        // 3. Convert DTO → Entity
        Mentor mentor = MentorMapper.toEntity(request);
        
        // 4. Save mentor
        Mentor savedMentor = mentorRepository.save(mentor);

        // 5. Save mentor skills
        List<MentorSkill> mentorSkills =
                MentorMapper.toMentorSkills(savedMentor.getMentorId(), request.getSkillIds());

        mentorSkillRepository.saveAll(mentorSkills);

        // 6. Prepare response
        List<Long> skillIds = mentorSkills.stream()
                .map(MentorSkill::getSkillId)
                .collect(Collectors.toList());

        return MentorMapper.toResponse(savedMentor, skillIds, skillClient);
    }

    // GET ALL MENTORS
    @Override
    public List<MentorResponse> getAllMentors() {

        List<Mentor> mentors = mentorRepository.findAll();

        return mentors.stream().map(mentor -> {

            List<Long> skillIds = mentorSkillRepository.findByMentorId(mentor.getMentorId())
                    .stream()
                    .map(MentorSkill::getSkillId)
                    .collect(Collectors.toList());

            return MentorMapper.toResponse(mentor, skillIds, skillClient);

        }).collect(Collectors.toList());
    }

    // GET MENTOR BY ID
    @Override
    public MentorResponse getMentorById(Long id) {

        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        List<Long> skillIds = mentorSkillRepository.findByMentorId(id)
                .stream()
                .map(MentorSkill::getSkillId)
                .collect(Collectors.toList());

        return MentorMapper.toResponse(mentor, skillIds, skillClient);

    }

    // ADD AVAILABILITY
    @Override
    public void addAvailability(AvailabilityRequest request) {

        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        Availability availability = Availability.builder()
                .mentorId(mentor.getMentorId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        availabilityRepository.save(availability);
    }

    //Approve Mentor - ADMIN ONLY
    @Override
    public ApprovedMentorResponse approveMentor(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = mentor.getUserId();
        userClient.updateUserRole(userId, "MENTOR");
        mentor.setStatus(MentorStatus.APPROVED);
        Mentor ment = mentorRepository.save(mentor);
        return MentorMapper.toApprovedResponse(ment);
    }

    @Override
    public void updateRating(Long mentorId, Double rating) {
        Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        mentor.setRating(rating);
        mentorRepository.save(mentor);
    }


}