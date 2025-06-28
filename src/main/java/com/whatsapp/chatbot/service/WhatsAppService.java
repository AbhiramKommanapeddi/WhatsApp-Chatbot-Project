package com.whatsapp.chatbot.service;

import com.whatsapp.chatbot.dto.WhatsAppOutboundMessage;
import com.whatsapp.chatbot.model.WhatsAppMessage;
import com.whatsapp.chatbot.repository.WhatsAppMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for handling WhatsApp Business API operations
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WhatsAppService {

    private final WhatsAppMessageRepository messageRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${whatsapp.api.base-url}")
    private String whatsappApiBaseUrl;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api.access-token}")
    private String accessToken;

    @Value("${whatsapp.api.mock-mode:false}")
    private boolean mockMode;

    /**
     * Send a text message via WhatsApp Business API
     */
    public Mono<Map<String, Object>> sendTextMessage(String to, String message) {
        log.info("Sending text message to: {} (Mock Mode: {})", to, mockMode);

        // Mock mode for testing without real API
        if (mockMode) {
            return createMockResponse(to, message, "text");
        }

        WhatsAppOutboundMessage outboundMessage = WhatsAppOutboundMessage.builder()
                .to(to)
                .type("text")
                .text(WhatsAppOutboundMessage.Text.builder()
                        .body(message)
                        .build())
                .build();

        return sendMessage(outboundMessage)
                .doOnSuccess(response -> saveOutboundMessage(to, message, "text", "SENT"))
                .doOnError(error -> {
                    log.error("Failed to send message to {}: {}", to, error.getMessage());
                    saveOutboundMessage(to, message, "text", "FAILED");
                });
    }

    /**
     * Send an interactive button message
     */
    public Mono<Map<String, Object>> sendButtonMessage(String to, String bodyText, 
                                                      String[] buttonIds, String[] buttonTitles) {
        log.info("Sending button message to: {} (Mock Mode: {})", to, mockMode);

        // Mock mode for testing without real API
        if (mockMode) {
            return createMockResponse(to, bodyText, "interactive");
        }

        WhatsAppOutboundMessage.Interactive.Action.Button[] buttons = 
            new WhatsAppOutboundMessage.Interactive.Action.Button[buttonIds.length];

        for (int i = 0; i < buttonIds.length; i++) {
            buttons[i] = WhatsAppOutboundMessage.Interactive.Action.Button.builder()
                    .reply(WhatsAppOutboundMessage.Interactive.Action.Button.Reply.builder()
                            .id(buttonIds[i])
                            .title(buttonTitles[i])
                            .build())
                    .build();
        }

        WhatsAppOutboundMessage outboundMessage = WhatsAppOutboundMessage.builder()
                .to(to)
                .type("interactive")
                .interactive(WhatsAppOutboundMessage.Interactive.builder()
                        .type("button")
                        .body(WhatsAppOutboundMessage.Interactive.Body.builder()
                                .text(bodyText)
                                .build())
                        .action(WhatsAppOutboundMessage.Interactive.Action.builder()
                                .buttons(buttons)
                                .build())
                        .build())
                .build();

        return sendMessage(outboundMessage)
                .doOnSuccess(response -> saveOutboundMessage(to, bodyText, "interactive", "SENT"))
                .doOnError(error -> {
                    log.error("Failed to send button message to {}: {}", to, error.getMessage());
                    saveOutboundMessage(to, bodyText, "interactive", "FAILED");
                });
    }

    /**
     * Send an interactive list message
     */
    public Mono<Map<String, Object>> sendListMessage(String to, String bodyText, String buttonText,
                                                    String sectionTitle, String[] optionIds, 
                                                    String[] optionTitles, String[] optionDescriptions) {
        log.info("Sending list message to: {}", to);

        WhatsAppOutboundMessage.Interactive.Action.Section.Row[] rows = 
            new WhatsAppOutboundMessage.Interactive.Action.Section.Row[optionIds.length];

        for (int i = 0; i < optionIds.length; i++) {
            rows[i] = WhatsAppOutboundMessage.Interactive.Action.Section.Row.builder()
                    .id(optionIds[i])
                    .title(optionTitles[i])
                    .description(optionDescriptions[i])
                    .build();
        }

        WhatsAppOutboundMessage.Interactive.Action.Section section = 
            WhatsAppOutboundMessage.Interactive.Action.Section.builder()
                    .title(sectionTitle)
                    .rows(rows)
                    .build();

        WhatsAppOutboundMessage outboundMessage = WhatsAppOutboundMessage.builder()
                .to(to)
                .type("interactive")
                .interactive(WhatsAppOutboundMessage.Interactive.builder()
                        .type("list")
                        .body(WhatsAppOutboundMessage.Interactive.Body.builder()
                                .text(bodyText)
                                .build())
                        .action(WhatsAppOutboundMessage.Interactive.Action.builder()
                                .button(buttonText)
                                .sections(new WhatsAppOutboundMessage.Interactive.Action.Section[]{section})
                                .build())
                        .build())
                .build();

        return sendMessage(outboundMessage)
                .doOnSuccess(response -> saveOutboundMessage(to, bodyText, "interactive", "SENT"))
                .doOnError(error -> {
                    log.error("Failed to send list message to {}: {}", to, error.getMessage());
                    saveOutboundMessage(to, bodyText, "interactive", "FAILED");
                });
    }

    /**
     * Core method to send message via WhatsApp API
     */
    private Mono<Map<String, Object>> sendMessage(WhatsAppOutboundMessage message) {
        WebClient webClient = webClientBuilder
                .baseUrl(whatsappApiBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return webClient.post()
                .uri("/{phoneNumberId}/messages", phoneNumberId)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (Map<String, Object>) response)
                .doOnSuccess(response -> log.info("Message sent successfully: {}", response))
                .doOnError(error -> log.error("Error sending message: {}", error.getMessage()));
    }

    /**
     * Save outbound message to database
     */
    private void saveOutboundMessage(String to, String messageText, String messageType, String status) {
        try {
            WhatsAppMessage message = new WhatsAppMessage();
            message.setFromNumber(phoneNumberId);
            message.setToNumber(to);
            message.setMessageText(messageText);
            message.setMessageType(messageType);
            message.setStatus(status);
            message.setDirection("OUTBOUND");
            message.setTimestamp(LocalDateTime.now());
            
            messageRepository.save(message);
            log.debug("Saved outbound message to database");
        } catch (Exception e) {
            log.error("Failed to save outbound message: {}", e.getMessage());
        }
    }

    /**
     * Create a mock response for testing purposes
     */
    private Mono<Map<String, Object>> createMockResponse(String to, String message, String messageType) {
        Map<String, Object> response = Map.of(
                "to", to,
                "message", message,
                "type", messageType,
                "status", "SENT",
                "timestamp", LocalDateTime.now()
        );

        log.info("Mock response created: {}", response);
        return Mono.just(response);
    }
}
