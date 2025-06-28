package com.whatsapp.chatbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.chatbot.dto.WebhookRequest;
import com.whatsapp.chatbot.service.ChatbotService;
import com.whatsapp.chatbot.service.FirebaseService;
import com.whatsapp.chatbot.repository.WhatsAppMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WhatsAppWebhookController
 */
@WebMvcTest(WhatsAppWebhookController.class)
class WhatsAppWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatbotService chatbotService;

    @MockBean
    private WhatsAppMessageRepository messageRepository;

    @MockBean
    private FirebaseService firebaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void verifyWebhook_Success() throws Exception {
        mockMvc.perform(get("/webhook")
                .param("hub.mode", "subscribe")
                .param("hub.challenge", "test_challenge")
                .param("hub.verify_token", "your_webhook_verify_token"))
                .andExpect(status().isOk())
                .andExpect(content().string("test_challenge"));
    }

    @Test
    void verifyWebhook_InvalidToken() throws Exception {
        mockMvc.perform(get("/webhook")
                .param("hub.mode", "subscribe")
                .param("hub.challenge", "test_challenge")
                .param("hub.verify_token", "invalid_token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void handleWebhook_Success() throws Exception {
        // Arrange
        WebhookRequest request = new WebhookRequest();
        request.setObject("whatsapp_business_account");
        request.setEntry(new WebhookRequest.Entry[0]);

        when(chatbotService.processMessage(anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());

        // Act & Assert
        mockMvc.perform(post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void healthCheck_Success() throws Exception {
        mockMvc.perform(get("/webhook/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Webhook is healthy"));
    }
}
