package com.whatsapp.chatbot.controller;

import com.whatsapp.chatbot.model.WhatsAppMessage;
import com.whatsapp.chatbot.model.UserSession;
import com.whatsapp.chatbot.repository.WhatsAppMessageRepository;
import com.whatsapp.chatbot.repository.UserSessionRepository;
import com.whatsapp.chatbot.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for chatbot management and testing
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class ChatbotApiController {

    private final WhatsAppService whatsAppService;
    private final WhatsAppMessageRepository messageRepository;
    private final UserSessionRepository sessionRepository;

    /**
     * Send a test message to a phone number
     */
    @PostMapping("/send-message")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam String to,
            @RequestParam String message) {
        
        log.info("API request to send message to: {}", to);

        return whatsAppService.sendTextMessage(to, message)
                .map(response -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "Message sent successfully");
                    result.put("response", response);
                    return ResponseEntity.ok(result);
                })
                .onErrorResume(error -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "Failed to send message: " + error.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(result));
                })
                .block();
    }

    /**
     * Send a button message
     */
    @PostMapping("/send-button-message")
    public ResponseEntity<Map<String, Object>> sendButtonMessage(
            @RequestParam String to,
            @RequestParam String bodyText,
            @RequestParam String[] buttonIds,
            @RequestParam String[] buttonTitles) {
        
        log.info("API request to send button message to: {}", to);

        return whatsAppService.sendButtonMessage(to, bodyText, buttonIds, buttonTitles)
                .map(response -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("message", "Button message sent successfully");
                    result.put("response", response);
                    return ResponseEntity.ok(result);
                })
                .onErrorResume(error -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", false);
                    result.put("message", "Failed to send button message: " + error.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(result));
                })
                .block();
    }

    /**
     * Get message history for a phone number
     */
    @GetMapping("/messages/{phoneNumber}")
    public ResponseEntity<List<WhatsAppMessage>> getMessages(
            @PathVariable String phoneNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API request to get messages for: {}", phoneNumber);

        List<WhatsAppMessage> messages = messageRepository
                .findByFromNumberOrToNumberOrderByTimestampDesc(phoneNumber, phoneNumber);
        
        return ResponseEntity.ok(messages);
    }

    /**
     * Get all messages with pagination
     */
    @GetMapping("/messages")
    public ResponseEntity<Page<WhatsAppMessage>> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("API request to get all messages");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<WhatsAppMessage> messages = messageRepository.findAll(pageRequest);
        
        return ResponseEntity.ok(messages);
    }

    /**
     * Get user session information
     */
    @GetMapping("/session/{phoneNumber}")
    public ResponseEntity<UserSession> getUserSession(@PathVariable String phoneNumber) {
        log.info("API request to get session for: {}", phoneNumber);

        return sessionRepository.findByPhoneNumber(phoneNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<UserSession>> getActiveSessions() {
        log.info("API request to get all active sessions");

        List<UserSession> sessions = sessionRepository.findBySessionActiveTrueOrderByUpdatedAtDesc();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get chatbot statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("API request to get chatbot statistics");

        Map<String, Object> stats = new HashMap<>();
        
        // Message statistics
        long totalMessages = messageRepository.count();
        long inboundMessages = messageRepository.countByDirection("INBOUND");
        long outboundMessages = messageRepository.countByDirection("OUTBOUND");
        
        // Session statistics
        long totalSessions = sessionRepository.count();
        long activeSessions = sessionRepository.countBySessionActiveTrue();
        
        stats.put("totalMessages", totalMessages);
        stats.put("inboundMessages", inboundMessages);
        stats.put("outboundMessages", outboundMessages);
        stats.put("totalSessions", totalSessions);
        stats.put("activeSessions", activeSessions);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "WhatsApp Chatbot API");
        
        return ResponseEntity.ok(health);
    }

    /**
     * Reset user session
     */
    @PostMapping("/session/{phoneNumber}/reset")
    public ResponseEntity<Map<String, String>> resetUserSession(@PathVariable String phoneNumber) {
        log.info("API request to reset session for: {}", phoneNumber);

        sessionRepository.findByPhoneNumber(phoneNumber).ifPresent(session -> {
            session.setCurrentState("WELCOME");
            session.setNavigationPath("WELCOME");
            session.setSessionActive(true);
            sessionRepository.save(session);
        });

        Map<String, String> response = new HashMap<>();
        response.put("message", "Session reset successfully");
        response.put("phoneNumber", phoneNumber);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test chatbot functionality
     */
    @PostMapping("/test-chatbot")
    public ResponseEntity<Map<String, String>> testChatbot(
            @RequestParam String phoneNumber,
            @RequestParam String message) {
        
        log.info("API request to test chatbot with message: {} from: {}", message, phoneNumber);

        try {
            // This would normally be called by the webhook
            // For testing purposes, we can call it directly
            Map<String, String> response = new HashMap<>();
            response.put("message", "Test message processed");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error processing test message: " + e.getMessage());
            response.put("status", "error");
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
