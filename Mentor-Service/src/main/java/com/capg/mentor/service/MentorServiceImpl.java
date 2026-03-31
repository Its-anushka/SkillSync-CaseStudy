package com.capg.mentor.service;

import com.capg.mentor.client.SkillClient;
import com.capg.mentor.client.UserClient;
import com.capg.mentor.dto.SkillDto;
import com.capg.mentor.dto.UserDto;
import com.capg.mentor.dto.request.AvailabilityRequest;
import com.capg.mentor.dto.request.MentorRequest;
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

/**
 * Mentor Service Implementation
 * Handles business logic for mentor-related operations
 * 
 * Exception Handling:
 * - ResourceNotFoundException: Thrown when mentor, user, or skill is not found (HTTP 404)
 * - BadRequestException: Thrown when user has already applied or availability times are invalid (HTTP 400)
 */
@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final MentorSkillRepository mentorSkillRepository;
    private final AvailabilityRepository availabilityRepository;

    private final UserClient userClient;
    private final SkillClient skillClient;

    private static final String message1 = "Mentor not found";
    /**
     * Apply for a mentorship role
     * 
     * @param request Mentor application request
     * @return MentorResponse containing mentor details
     * @throws BadRequestException if user already applied
     * @throws ResourceNotFoundException if user or skills do not exist
     */
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
                .toList();

        return MentorMapper.toResponse(savedMentor, skillIds, skillClient);
    }

    /**
     * Retrieve all approved mentors
     * 
     * @return List of MentorResponse objects
     */
    @Override
    public List<MentorResponse> getAllMentors() {

        List<Mentor> mentors = mentorRepository.findAll();

        return mentors.stream().map(mentor -> {

            List<Long> skillIds = mentorSkillRepository.findByMentorId(mentor.getMentorId())
                    .stream()
                    .map(MentorSkill::getSkillId)
                    .toList();

            return MentorMapper.toResponse(mentor, skillIds, skillClient);

        }).toList();
    }

    /**
     * Retrieve a specific mentor by ID
     * 
     * @param id Mentor ID
     * @return MentorResponse containing mentor details
     * @throws ResourceNotFoundException if mentor is not found
     */
    @Override
    public MentorResponse getMentorById(Long id) {

        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(message1));

        List<Long> skillIds = mentorSkillRepository.findByMentorId(id)
                .stream()
                .map(MentorSkill::getSkillId)
                .toList();

        return MentorMapper.toResponse(mentor, skillIds, skillClient);

    }

    /**
     * Add availability slot for a mentor
     * 
     * @param request Availability limits and details
     * @throws ResourceNotFoundException if mentor is not found
     * @throws BadRequestException if start time is after end time
     */
    @Override
    public void addAvailability(AvailabilityRequest request) {

        Mentor mentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException(message1));

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

    /**
     * Approve a mentor application (Admin only)
     * 
     * @param mentorId Mentor ID
     * @return ApprovedMentorResponse indicating success
     * @throws ResourceNotFoundException if mentor is not found
     */
    @Override
    public ApprovedMentorResponse approveMentor(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = mentor.getUserId();
        userClient.updateUserRole(userId, "MENTOR");
        mentor.setStatus(MentorStatus.APPROVED);
        Mentor ment = mentorRepository.save(mentor);
        return MentorMapper.toApprovedResponse(ment);
    }

    /**
     * Deny a mentor application (Admin only)
     * 
     * @param mentorId Mentor ID
     * @throws ResourceNotFoundException if mentor is not found
     */

    public void denyMentor(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(() -> new ResourceNotFoundException(message1));
        mentor.setStatus(MentorStatus.REJECTED);
        mentorRepository.save(mentor);
    }

    /**
     * Update the rating of a mentor
     * 
     * @param mentorId Mentor ID
     * @param rating New rating value
     * @throws ResourceNotFoundException if mentor is not found
     */
    @Override
    public void updateRating(Long mentorId, Double rating) {
        Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        mentor.setRating(rating);
        mentorRepository.save(mentor);
    }


}