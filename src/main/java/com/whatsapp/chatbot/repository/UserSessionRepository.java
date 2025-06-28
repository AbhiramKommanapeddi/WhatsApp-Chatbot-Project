package com.whatsapp.chatbot.repository;

import com.whatsapp.chatbot.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for user session operations
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Find active session by phone number
     */
    Optional<UserSession> findByPhoneNumberAndSessionActiveTrue(String phoneNumber);

    /**
     * Find session by phone number (active or inactive)
     */
    Optional<UserSession> findByPhoneNumber(String phoneNumber);

    /**
     * Find all active sessions
     */
    List<UserSession> findBySessionActiveTrueOrderByUpdatedAtDesc();

    /**
     * Find sessions updated after a specific time
     */
    List<UserSession> findByUpdatedAtAfterAndSessionActiveTrue(LocalDateTime since);

    /**
     * Deactivate old sessions
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.sessionActive = false WHERE s.updatedAt < :cutoffTime")
    int deactivateOldSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Count active sessions
     */
    long countBySessionActiveTrue();

    /**
     * Find sessions by current state
     */
    List<UserSession> findByCurrentStateAndSessionActiveTrue(String currentState);
}
