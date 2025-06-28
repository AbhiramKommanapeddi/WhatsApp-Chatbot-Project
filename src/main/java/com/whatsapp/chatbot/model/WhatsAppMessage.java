package com.whatsapp.chatbot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing a WhatsApp message
 * Stores message data for tracking and analytics
 */
@Entity
@Table(name = "whatsapp_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", unique = true, nullable = false)
    private String messageId;

    @Column(name = "from_number", nullable = false)
    private String fromNumber;

    @Column(name = "to_number", nullable = false)
    private String toNumber;

    @Column(name = "message_text", columnDefinition = "TEXT")
    private String messageText;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "status")
    private String status;

    @Column(name = "direction")
    private String direction; // INBOUND, OUTBOUND

    @Column(name = "conversation_id")
    private String conversationId;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
