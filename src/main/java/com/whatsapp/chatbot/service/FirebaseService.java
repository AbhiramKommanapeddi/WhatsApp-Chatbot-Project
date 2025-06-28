package com.whatsapp.chatbot.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Service for Firebase Firestore operations
 */
@Service
@Slf4j
public class FirebaseService {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${firebase.database.url}")
    private String firebaseDatabaseUrl;

    private Firestore firestore;

    @PostConstruct
    public void initialize() {
        try {
            initializeFirebase();
            log.info("Firebase service initialized successfully");
        } catch (Exception e) {
            log.warn("Firebase initialization failed: {}. Running without Firebase integration.", e.getMessage());
        }
    }

    /**
     * Initialize Firebase connection
     */
    private void initializeFirebase() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream(firebaseConfigPath));

        FirestoreOptions options = FirestoreOptions.newBuilder()
                .setCredentials(credentials)
                .build();

        firestore = options.getService();
    }

    /**
     * Save user interaction data to Firestore
     */
    public void saveUserInteraction(String phoneNumber, String messageText, String messageType, String timestamp) {
        if (firestore == null) {
            log.debug("Firestore not initialized, skipping save operation");
            return;
        }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("phoneNumber", phoneNumber);
            data.put("messageText", messageText);
            data.put("messageType", messageType);
            data.put("timestamp", timestamp);
            data.put("platform", "whatsapp");

            firestore.collection("user_interactions")
                    .add(data)
                    .get();

            log.debug("Saved user interaction to Firestore");
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to save user interaction to Firestore: {}", e.getMessage());
        }
    }

    /**
     * Save navigation request to Firestore
     */
    public void saveNavigationRequest(String phoneNumber, String fromLocation, String toLocation, String requestType) {
        if (firestore == null) {
            log.debug("Firestore not initialized, skipping save operation");
            return;
        }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("phoneNumber", phoneNumber);
            data.put("fromLocation", fromLocation);
            data.put("toLocation", toLocation);
            data.put("requestType", requestType);
            data.put("timestamp", System.currentTimeMillis());

            firestore.collection("navigation_requests")
                    .add(data)
                    .get();

            log.debug("Saved navigation request to Firestore");
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to save navigation request to Firestore: {}", e.getMessage());
        }
    }

    /**
     * Save user preferences to Firestore
     */
    public void saveUserPreferences(String phoneNumber, Map<String, Object> preferences) {
        if (firestore == null) {
            log.debug("Firestore not initialized, skipping save operation");
            return;
        }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("phoneNumber", phoneNumber);
            data.put("preferences", preferences);
            data.put("updatedAt", System.currentTimeMillis());

            firestore.collection("user_preferences")
                    .document(phoneNumber)
                    .set(data)
                    .get();

            log.debug("Saved user preferences to Firestore");
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to save user preferences to Firestore: {}", e.getMessage());
        }
    }

    /**
     * Get user preferences from Firestore
     */
    public Map<String, Object> getUserPreferences(String phoneNumber) {
        if (firestore == null) {
            log.debug("Firestore not initialized, returning empty preferences");
            return new HashMap<>();
        }

        try {
            var document = firestore.collection("user_preferences")
                    .document(phoneNumber)
                    .get()
                    .get();

            if (document.exists()) {
                Map<String, Object> data = document.getData();
                return (Map<String, Object>) data.get("preferences");
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get user preferences from Firestore: {}", e.getMessage());
        }

        return new HashMap<>();
    }

    /**
     * Save analytics data to Firestore
     */
    public void saveAnalyticsData(String eventType, Map<String, Object> eventData) {
        if (firestore == null) {
            log.debug("Firestore not initialized, skipping analytics save");
            return;
        }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("eventType", eventType);
            data.put("eventData", eventData);
            data.put("timestamp", System.currentTimeMillis());

            firestore.collection("analytics")
                    .add(data)
                    .get();

            log.debug("Saved analytics data to Firestore");
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to save analytics data to Firestore: {}", e.getMessage());
        }
    }

    /**
     * Check if Firebase is properly initialized
     */
    public boolean isFirebaseInitialized() {
        return firestore != null;
    }
}
