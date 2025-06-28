package com.whatsapp.chatbot.service;

import com.whatsapp.chatbot.model.UserSession;
import com.whatsapp.chatbot.repository.UserSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserSessionService
 */
@ExtendWith(MockitoExtension.class)
class UserSessionServiceTest {

    @Mock
    private UserSessionRepository sessionRepository;

    @InjectMocks
    private UserSessionService userSessionService;

    @Test
    void getOrCreateSession_ExistingSession() {
        // Arrange
        String phoneNumber = "1234567890";
        UserSession existingSession = new UserSession();
        existingSession.setPhoneNumber(phoneNumber);
        existingSession.setCurrentState("MAIN_MENU");
        existingSession.setSessionActive(true);

        when(sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber))
                .thenReturn(Optional.of(existingSession));
        when(sessionRepository.save(any(UserSession.class))).thenReturn(existingSession);

        // Act
        UserSession result = userSessionService.getOrCreateSession(phoneNumber);

        // Assert
        assertNotNull(result);
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals("MAIN_MENU", result.getCurrentState());
        assertTrue(result.getSessionActive());
        verify(sessionRepository).save(existingSession);
    }

    @Test
    void getOrCreateSession_NewSession() {
        // Arrange
        String phoneNumber = "1234567890";
        UserSession newSession = new UserSession();
        newSession.setPhoneNumber(phoneNumber);
        newSession.setCurrentState("WELCOME");
        newSession.setSessionActive(true);

        when(sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any(UserSession.class))).thenReturn(newSession);

        // Act
        UserSession result = userSessionService.getOrCreateSession(phoneNumber);

        // Assert
        assertNotNull(result);
        verify(sessionRepository).save(any(UserSession.class));
    }

    @Test
    void updateSessionState_Success() {
        // Arrange
        String phoneNumber = "1234567890";
        String newState = "NAVIGATION_HELP";
        UserSession session = new UserSession();
        session.setPhoneNumber(phoneNumber);
        session.setCurrentState("WELCOME");
        session.setNavigationPath("WELCOME");
        session.setSessionActive(true);

        when(sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber))
                .thenReturn(Optional.of(session));
        when(sessionRepository.save(any(UserSession.class))).thenReturn(session);

        // Act
        UserSession result = userSessionService.updateSessionState(phoneNumber, newState);

        // Assert
        assertNotNull(result);
        assertEquals(newState, result.getCurrentState());
        assertTrue(result.getNavigationPath().contains(newState));
        verify(sessionRepository).save(session);
    }

    @Test
    void getCurrentState_ExistingSession() {
        // Arrange
        String phoneNumber = "1234567890";
        String expectedState = "MAIN_MENU";
        UserSession session = new UserSession();
        session.setCurrentState(expectedState);

        when(sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber))
                .thenReturn(Optional.of(session));

        // Act
        String result = userSessionService.getCurrentState(phoneNumber);

        // Assert
        assertEquals(expectedState, result);
    }

    @Test
    void getCurrentState_NoSession() {
        // Arrange
        String phoneNumber = "1234567890";

        when(sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber))
                .thenReturn(Optional.empty());

        // Act
        String result = userSessionService.getCurrentState(phoneNumber);

        // Assert
        assertEquals("WELCOME", result);
    }

    @Test
    void endSession_Success() {
        // Arrange
        String phoneNumber = "1234567890";
        UserSession session = new UserSession();
        session.setPhoneNumber(phoneNumber);
        session.setSessionActive(true);

        when(sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber))
                .thenReturn(Optional.of(session));
        when(sessionRepository.save(any(UserSession.class))).thenReturn(session);

        // Act
        userSessionService.endSession(phoneNumber);

        // Assert
        assertFalse(session.getSessionActive());
        verify(sessionRepository).save(session);
    }
}
