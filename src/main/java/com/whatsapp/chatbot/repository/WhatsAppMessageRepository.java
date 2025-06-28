package com.whatsapp.chatbot.repository;

import com.whatsapp.chatbot.model.WhatsAppMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WhatsApp message operations
 */
@Repository
public interface WhatsAppMessageRepository extends JpaRepository<WhatsAppMessage, Long> {

    /**
     * Find message by WhatsApp message ID
     */
    Optional<WhatsAppMessage> findByMessageId(String messageId);

    /**
     * Find all messages for a specific phone number
     */
    List<WhatsAppMessage> findByFromNumberOrToNumberOrderByTimestampDesc(String fromNumber, String toNumber);

    /**
     * Find messages by conversation ID
     */
    List<WhatsAppMessage> findByConversationIdOrderByTimestampAsc(String conversationId);

    /**
     * Find recent messages for a phone number
     */
    @Query("SELECT m FROM WhatsAppMessage m WHERE (m.fromNumber = :phoneNumber OR m.toNumber = :phoneNumber) " +
           "AND m.timestamp >= :since ORDER BY m.timestamp DESC")
    List<WhatsAppMessage> findRecentMessagesByPhoneNumber(@Param("phoneNumber") String phoneNumber, 
                                                          @Param("since") LocalDateTime since);

    /**
     * Count messages by status
     */
    long countByStatus(String status);

    /**
     * Find messages by direction (INBOUND/OUTBOUND)
     */
    List<WhatsAppMessage> findByDirectionOrderByTimestampDesc(String direction);

    /**
     * Count messages by direction (INBOUND/OUTBOUND)
     */
    long countByDirection(String direction);
}
