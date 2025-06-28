package com.whatsapp.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for WhatsApp Chatbot
 * 
 * This is the entry point of the Spring Boot application that handles
 * WhatsApp Business API integration, Firebase services, and navigation workflows.
 * 
 * @author WhatsApp Chatbot Team
 * @version 1.0.0
 */
@SpringBootApplication
public class WhatsAppChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsAppChatbotApplication.class, args);
    }
}
