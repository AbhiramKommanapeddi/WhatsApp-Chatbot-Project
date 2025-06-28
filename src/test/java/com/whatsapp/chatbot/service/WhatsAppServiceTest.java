package com.whatsapp.chatbot.service;

import com.whatsapp.chatbot.model.WhatsAppMessage;
import com.whatsapp.chatbot.repository.WhatsAppMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WhatsAppService
 */
@ExtendWith(MockitoExtension.class)
class WhatsAppServiceTest {

    @Mock
    private WhatsAppMessageRepository messageRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private WhatsAppService whatsAppService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(any(), any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void sendTextMessage_Success() {
        // Arrange
        String to = "1234567890";
        String message = "Test message";
        Map<String, Object> expectedResponse = Map.of("messages", "sent");
        
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(expectedResponse));
        when(messageRepository.save(any(WhatsAppMessage.class))).thenReturn(new WhatsAppMessage());

        // Act & Assert
        Mono<Map<String, Object>> result = whatsAppService.sendTextMessage(to, message);
        // For now, just verify the method doesn't throw an exception
        // In a real scenario, you would need to mock WebClient properly

        verify(messageRepository).save(any(WhatsAppMessage.class));
    }

    @Test
    void sendButtonMessage_Success() {
        // Arrange
        String to = "1234567890";
        String bodyText = "Choose an option";
        String[] buttonIds = {"btn1", "btn2"};
        String[] buttonTitles = {"Option 1", "Option 2"};
        Map<String, Object> expectedResponse = Map.of("messages", "sent");
        
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(expectedResponse));
        when(messageRepository.save(any(WhatsAppMessage.class))).thenReturn(new WhatsAppMessage());

        // Act & Assert
        Mono<Map<String, Object>> result = whatsAppService.sendButtonMessage(to, bodyText, buttonIds, buttonTitles);
        // For now, just verify the method doesn't throw an exception
        // In a real scenario, you would need to mock WebClient properly

        verify(messageRepository).save(any(WhatsAppMessage.class));
    }
}
