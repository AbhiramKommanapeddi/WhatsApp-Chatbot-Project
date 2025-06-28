package com.whatsapp.chatbot.service;

import com.whatsapp.chatbot.model.UserSession;
import com.whatsapp.chatbot.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing user sessions and navigation state
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository sessionRepository;

    /**
     * Get or create user session
     */
    @Transactional
    public UserSession getOrCreateSession(String phoneNumber) {
        log.debug("Getting or creating session for phone number: {}", phoneNumber);

        Optional<UserSession> existingSession = sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber);
        
        if (existingSession.isPresent()) {
            UserSession session = existingSession.get();
            session.setUpdatedAt(LocalDateTime.now());
            return sessionRepository.save(session);
        }

        // Create new session
        UserSession newSession = new UserSession();
        newSession.setPhoneNumber(phoneNumber);
        newSession.setCurrentState("WELCOME");
        newSession.setNavigationPath("WELCOME");
        newSession.setSessionActive(true);
        
        return sessionRepository.save(newSession);
    }

    /**
     * Update user session state
     */
    @Transactional
    public UserSession updateSessionState(String phoneNumber, String newState) {
        log.debug("Updating session state for {}: {}", phoneNumber, newState);

        UserSession session = getOrCreateSession(phoneNumber);
        
        // Update navigation path
        String currentPath = session.getNavigationPath();
        if (currentPath == null || currentPath.isEmpty()) {
            session.setNavigationPath(newState);
        } else {
            session.setNavigationPath(currentPath + " -> " + newState);
        }
        
        session.setCurrentState(newState);
        session.setUpdatedAt(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }

    /**
     * Update user preferences
     */
    @Transactional
    public UserSession updateUserPreferences(String phoneNumber, String preferences) {
        log.debug("Updating user preferences for {}: {}", phoneNumber, preferences);

        UserSession session = getOrCreateSession(phoneNumber);
        session.setUserPreferences(preferences);
        session.setUpdatedAt(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }

    /**
     * End user session
     */
    @Transactional
    public void endSession(String phoneNumber) {
        log.debug("Ending session for phone number: {}", phoneNumber);

        Optional<UserSession> session = sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber);
        if (session.isPresent()) {
            UserSession userSession = session.get();
            userSession.setSessionActive(false);
            userSession.setUpdatedAt(LocalDateTime.now());
            sessionRepository.save(userSession);
        }
    }

    /**
     * Get current session state
     */
    public String getCurrentState(String phoneNumber) {
        Optional<UserSession> session = sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber);
        return session.map(UserSession::getCurrentState).orElse("WELCOME");
    }

    /**
     * Get user preferences
     */
    public String getUserPreferences(String phoneNumber) {
        Optional<UserSession> session = sessionRepository.findByPhoneNumberAndSessionActiveTrue(phoneNumber);
        return session.map(UserSession::getUserPreferences).orElse("");
    }

    /**
     * Clean up old inactive sessions
     */
    @Transactional
    public int cleanupOldSessions() {
        log.info("Cleaning up old sessions");
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        return sessionRepository.deactivateOldSessions(cutoffTime);
    }

    /**
     * Get session statistics
     */
    public long getActiveSessionCount() {
        return sessionRepository.countBySessionActiveTrue();
    }
}
