package com.whatsapp.chatbot.controller;

import com.whatsapp.chatbot.dto.WebhookRequest;
import com.whatsapp.chatbot.model.WhatsAppMessage;
import com.whatsapp.chatbot.repository.WhatsAppMessageRepository;
import com.whatsapp.chatbot.service.ChatbotService;
import com.whatsapp.chatbot.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Controller for handling WhatsApp webhook requests
 */
@RestController
@RequestMapping("/webhook")
@Slf4j
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private final ChatbotService chatbotService;
    private final WhatsAppMessageRepository messageRepository;
    private final FirebaseService firebaseService;

    @Value("${whatsapp.webhook.verify-token}")
    private String verifyToken;

    /**
     * Webhook verification endpoint (GET)
     * WhatsApp sends a GET request to verify the webhook
     */
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {
        
        log.info("Webhook verification request received");
        log.debug("Mode: {}, Challenge: {}, Token: {}", mode, challenge, token);

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            log.info("Webhook verified successfully");
            return ResponseEntity.ok(challenge);
        } else {
            log.warn("Webhook verification failed - token mismatch");
            return ResponseEntity.status(403).body("Verification failed");
        }
    }

    /**
     * Webhook message handler (POST)
     * WhatsApp sends incoming messages and status updates here
     */
    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookRequest request) {
        log.info("Webhook message received");
        log.debug("Webhook payload: {}", request);

        try {
            processWebhookRequest(request);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error processing webhook request: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing request");
        }
    }

    /**
     * Process the incoming webhook request
     */
    private void processWebhookRequest(WebhookRequest request) {
        if (request.getEntry() == null || request.getEntry().length == 0) {
            log.warn("No entries found in webhook request");
            return;
        }

        Arrays.stream(request.getEntry()).forEach(entry -> {
            if (entry.getChanges() != null) {
                Arrays.stream(entry.getChanges()).forEach(change -> {
                    if ("messages".equals(change.getField()) && change.getValue() != null) {
                        processMessages(change.getValue());
                        processStatuses(change.getValue());
                    }
                });
            }
        });
    }

    /**
     * Process incoming messages
     */
    private void processMessages(WebhookRequest.Entry.Change.Value value) {
        if (value.getMessages() == null) {
            return;
        }

        Arrays.stream(value.getMessages()).forEach(message -> {
            log.info("Processing message: {} from {}", message.getId(), message.getFrom());

            // Save message to database
            saveInboundMessage(message);

            // Save to Firebase for analytics
            saveToFirebase(message);

            // Extract message text
            String messageText = extractMessageText(message);
            
            // Process the message through chatbot service
            if (messageText != null && !messageText.trim().isEmpty()) {
                chatbotService.processMessage(message.getFrom(), messageText, message.getType())
                        .subscribe(
                            null,
                            error -> log.error("Error processing message {}: {}", message.getId(), error.getMessage())
                        );
            }
        });
    }

    /**
     * Process message status updates
     */
    private void processStatuses(WebhookRequest.Entry.Change.Value value) {
        if (value.getStatuses() == null) {
            return;
        }

        Arrays.stream(value.getStatuses()).forEach(status -> {
            log.debug("Message status update: {} - {}", status.getId(), status.getStatus());
            
            // Update message status in database
            messageRepository.findByMessageId(status.getId()).ifPresent(message -> {
                message.setStatus(status.getStatus());
                messageRepository.save(message);
            });
        });
    }

    /**
     * Save inbound message to database
     */
    private void saveInboundMessage(WebhookRequest.Entry.Change.Value.Message message) {
        try {
            WhatsAppMessage dbMessage = new WhatsAppMessage();
            dbMessage.setMessageId(message.getId());
            dbMessage.setFromNumber(message.getFrom());
            dbMessage.setToNumber("chatbot"); // This would be your phone number ID
            dbMessage.setMessageText(extractMessageText(message));
            dbMessage.setMessageType(message.getType());
            dbMessage.setStatus("RECEIVED");
            dbMessage.setDirection("INBOUND");
            dbMessage.setTimestamp(LocalDateTime.now());
            
            messageRepository.save(dbMessage);
            log.debug("Saved inbound message to database: {}", message.getId());
        } catch (Exception e) {
            log.error("Failed to save inbound message: {}", e.getMessage());
        }
    }

    /**
     * Save message data to Firebase
     */
    private void saveToFirebase(WebhookRequest.Entry.Change.Value.Message message) {
        try {
            String messageText = extractMessageText(message);
            firebaseService.saveUserInteraction(
                message.getFrom(),
                messageText,
                message.getType(),
                message.getTimestamp()
            );
        } catch (Exception e) {
            log.error("Failed to save to Firebase: {}", e.getMessage());
        }
    }

    /**
     * Extract message text from different message types
     */
    private String extractMessageText(WebhookRequest.Entry.Change.Value.Message message) {
        if (message.getText() != null) {
            return message.getText().getBody();
        }
        
        if (message.getInteractive() != null) {
            var interactive = message.getInteractive();
            if (interactive.getButton_reply() != null) {
                return interactive.getButton_reply().getId();
            }
            if (interactive.getList_reply() != null) {
                return interactive.getList_reply().getId();
            }
        }
        
        return null;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Webhook is healthy");
    }
}
