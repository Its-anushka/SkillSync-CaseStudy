package com.capg.session.service;

import com.capg.session.bean.Session;
import com.capg.session.client.MentorServiceClient;
import com.capg.session.client.UserServiceClient;
import com.capg.session.dao.SessionDAO;
import com.capg.session.dto.ApiResponse;
import com.capg.session.dto.MentorResponse;
import com.capg.session.dto.UserDto;
import com.capg.session.event.SessionEvent;
import com.capg.session.event.SessionEventPublisher;
import com.capg.session.exception.InvalidSessionException;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionDAO dao;

    @Mock
    private SessionEventPublisher publisher;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private MentorServiceClient mentorServiceClient;

    @InjectMocks
    private SessionService sessionService;

    private Session dummySession;
    private Date futureDate;

    @BeforeEach
    void setUp() {
        futureDate = new Date(System.currentTimeMillis() + 86400000); // +1 day
        dummySession = new Session();
        dummySession.setId(1);
        dummySession.setLearner_id(10);
        dummySession.setMentor_id(20);
        dummySession.setSession_Date(futureDate);
        dummySession.setStatus("REQUESTED");
    }

    @Test
    void requestSessionService_ValidData_Success() {
        UserDto mockUser = new UserDto();
        mockUser.setId(10L);

        MentorResponse mockMentor = new MentorResponse();
        mockMentor.setId(20L);
        ApiResponse<MentorResponse> mentorApiResponse = new ApiResponse<>();
        mentorApiResponse.setData(mockMentor);

        when(userServiceClient.getUserById(10L)).thenReturn(mockUser);
        when(mentorServiceClient.getMentorById(20L)).thenReturn(mentorApiResponse);
        when(dao.requestSession(20, 10, futureDate)).thenReturn(dummySession);

        Session result = sessionService.requestSessionService(20, 10, futureDate);

        assertNotNull(result);
        assertEquals("REQUESTED", result.getStatus());
        
        verify(userServiceClient, times(1)).getUserById(10L);
        verify(mentorServiceClient, times(1)).getMentorById(20L);
        verify(dao, times(1)).requestSession(20, 10, futureDate);
        verify(publisher, times(1)).publish(any(SessionEvent.class));
    }

    @Test
    void requestSessionService_InvalidUser_ThrowsException() {
        when(userServiceClient.getUserById(10L)).thenThrow(FeignException.NotFound.class);

        InvalidSessionException ex = assertThrows(InvalidSessionException.class, () -> {
            sessionService.requestSessionService(20, 10, futureDate);
        });

        assertEquals("Learner ID is invalid or user does not exist", ex.getMessage());
        verify(dao, never()).requestSession(anyInt(), anyInt(), any(Date.class));
        verify(publisher, never()).publish(any(SessionEvent.class));
    }

    @Test
    void requestSessionService_InvalidMentor_ThrowsException() {
        UserDto mockUser = new UserDto();
        when(userServiceClient.getUserById(10L)).thenReturn(mockUser);
        when(mentorServiceClient.getMentorById(20L)).thenThrow(FeignException.NotFound.class);

        InvalidSessionException ex = assertThrows(InvalidSessionException.class, () -> {
            sessionService.requestSessionService(20, 10, futureDate);
        });

        assertEquals("Mentor ID is invalid or mentor does not exist", ex.getMessage());
        verify(dao, never()).requestSession(anyInt(), anyInt(), any(Date.class));
    }

    @Test
    void requestSessionService_PastDate_ThrowsException() {
        Date pastDate = new Date(System.currentTimeMillis() - 86400000); // -1 day

        InvalidSessionException ex = assertThrows(InvalidSessionException.class, () -> {
            sessionService.requestSessionService(20, 10, pastDate);
        });

        assertEquals("Session date cannot be null or in the past", ex.getMessage());
    }

    @Test
    void acceptSessionService_Success() {
        dummySession.setStatus("ACCEPTED");
        when(dao.acceptSession(1)).thenReturn(dummySession);

        Session result = sessionService.acceptSessionService(1);

        assertNotNull(result);
        assertEquals("ACCEPTED", result.getStatus());
        
        verify(dao, times(1)).acceptSession(1);
        verify(publisher, times(1)).publish(any(SessionEvent.class));
    }

    @Test
    void bookSessionService_ValidData_Success() {
        UserDto mockUser = new UserDto();
        mockUser.setId(10L);

        MentorResponse mockMentor = new MentorResponse();
        mockMentor.setId(20L);
        ApiResponse<MentorResponse> mentorApiResponse = new ApiResponse<>();
        mentorApiResponse.setData(mockMentor);

        when(userServiceClient.getUserById(10L)).thenReturn(mockUser);
        when(mentorServiceClient.getMentorById(20L)).thenReturn(mentorApiResponse);
        
        Session bookedSession = new Session();
        bookedSession.setId(2);
        bookedSession.setStatus("BOOKED");
        when(dao.bookSession(20, 10, futureDate)).thenReturn(bookedSession);

        Session result = sessionService.bookSessionService(20, 10, futureDate);

        assertNotNull(result);
        assertEquals("BOOKED", result.getStatus());
        
        verify(userServiceClient, times(1)).getUserById(10L);
        verify(mentorServiceClient, times(1)).getMentorById(20L);
        verify(dao, times(1)).bookSession(20, 10, futureDate);
        verify(publisher, times(1)).publish(any(SessionEvent.class));
    }

    @Test
    void getMySessionService_Success() {
        java.util.List<Session> sessions = java.util.Arrays.asList(dummySession);
        when(dao.getSessionsByUserId(10)).thenReturn(sessions);

        java.util.List<Session> result = sessionService.getMySessionService(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dao, times(1)).getSessionsByUserId(10);
    }

    @Test
    void getSessionByIdService_Success() {
        when(dao.getSessionById(1)).thenReturn(dummySession);

        Session result = sessionService.getSessionByIdService(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(dao, times(1)).getSessionById(1);
    }
}
