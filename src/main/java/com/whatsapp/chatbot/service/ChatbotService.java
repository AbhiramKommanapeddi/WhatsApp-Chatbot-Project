package com.whatsapp.chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service for handling chatbot conversation logic and navigation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotService {

    private final WhatsAppService whatsAppService;
    private final UserSessionService sessionService;

    /**
     * Process incoming message and generate appropriate response
     */
    public Mono<Void> processMessage(String fromNumber, String messageText, String messageType) {
        log.info("Processing message from {}: {} (type: {})", fromNumber, messageText, messageType);

        String currentState = sessionService.getCurrentState(fromNumber);
        log.debug("Current user state: {}", currentState);

        return handleConversationFlow(fromNumber, messageText, currentState)
                .doOnSuccess(v -> log.info("Successfully processed message from {}", fromNumber))
                .doOnError(error -> log.error("Error processing message from {}: {}", fromNumber, error.getMessage()));
    }

    /**
     * Handle conversation flow based on current state
     */
    private Mono<Void> handleConversationFlow(String phoneNumber, String userInput, String currentState) {
        switch (currentState) {
            case "WELCOME":
                return handleWelcomeState(phoneNumber);
                
            case "MAIN_MENU":
                return handleMainMenuState(phoneNumber, userInput);
                
            case "NAVIGATION_HELP":
                return handleNavigationHelpState(phoneNumber, userInput);
                
            case "LOCATION_SEARCH":
                return handleLocationSearchState(phoneNumber, userInput);
                
            case "ROUTE_PLANNING":
                return handleRoutePlanningState(phoneNumber, userInput);
                
            case "TRAFFIC_INFO":
                return handleTrafficInfoState(phoneNumber, userInput);
                
            case "SETTINGS":
                return handleSettingsState(phoneNumber, userInput);
                
            default:
                return handleWelcomeState(phoneNumber);
        }
    }

    /**
     * Handle welcome state - send initial greeting and main menu
     */
    private Mono<Void> handleWelcomeState(String phoneNumber) {
        sessionService.updateSessionState(phoneNumber, "MAIN_MENU");
        
        String welcomeMessage = "🚗 Welcome to Navigation Assistant! 🗺️\n\n" +
                               "I'm here to help you with directions, traffic updates, and route planning.\n\n" +
                               "What would you like to do today?";
        
        String[] buttonIds = {"navigation_help", "find_location", "traffic_info"};
        String[] buttonTitles = {"🧭 Navigation Help", "📍 Find Location", "🚦 Traffic Info"};
        
        return whatsAppService.sendButtonMessage(phoneNumber, welcomeMessage, buttonIds, buttonTitles)
                .then();
    }

    /**
     * Handle main menu state
     */
    private Mono<Void> handleMainMenuState(String phoneNumber, String userInput) {
        switch (userInput.toLowerCase()) {
            case "navigation_help":
                return handleNavigationHelp(phoneNumber);
                
            case "find_location":
                return handleLocationSearch(phoneNumber);
                
            case "traffic_info":
                return handleTrafficInfo(phoneNumber);
                
            default:
                return showMainMenu(phoneNumber);
        }
    }

    /**
     * Handle navigation help flow
     */
    private Mono<Void> handleNavigationHelp(String phoneNumber) {
        sessionService.updateSessionState(phoneNumber, "NAVIGATION_HELP");
        
        String helpMessage = "🧭 Navigation Help\n\n" +
                           "I can help you with:\n" +
                           "• Step-by-step directions\n" +
                           "• Route optimization\n" +
                           "• Alternative routes\n" +
                           "• Real-time navigation\n\n" +
                           "What type of navigation help do you need?";
        
        String[] optionIds = {"get_directions", "optimize_route", "alternative_routes", "real_time_nav"};
        String[] optionTitles = {"Get Directions", "Optimize Route", "Alternative Routes", "Real-time Navigation"};
        String[] optionDescriptions = {
            "Get step-by-step directions",
            "Find the fastest route",
            "Explore different route options",
            "Live navigation assistance"
        };
        
        return whatsAppService.sendListMessage(phoneNumber, helpMessage, "Select Option", 
                                             "Navigation Services", optionIds, optionTitles, optionDescriptions)
                .then();
    }

    /**
     * Handle location search flow
     */
    private Mono<Void> handleLocationSearch(String phoneNumber) {
        sessionService.updateSessionState(phoneNumber, "LOCATION_SEARCH");
        
        String searchMessage = "📍 Location Search\n\n" +
                             "Please type the location you're looking for:\n" +
                             "• Business name (e.g., 'Starbucks')\n" +
                             "• Full address\n" +
                             "• Landmark or POI\n\n" +
                             "Example: 'Central Park, New York' or 'nearest gas station'";
        
        return whatsAppService.sendTextMessage(phoneNumber, searchMessage)
                .then();
    }

    /**
     * Handle traffic info flow
     */
    private Mono<Void> handleTrafficInfo(String phoneNumber) {
        sessionService.updateSessionState(phoneNumber, "TRAFFIC_INFO");
        
        String trafficMessage = "🚦 Traffic Information\n\n" +
                              "Get real-time traffic updates for your area.\n\n" +
                              "What would you like to know?";
        
        String[] buttonIds = {"current_traffic", "route_traffic", "traffic_alerts"};
        String[] buttonTitles = {"Current Traffic", "Route Traffic", "Traffic Alerts"};
        
        return whatsAppService.sendButtonMessage(phoneNumber, trafficMessage, buttonIds, buttonTitles)
                .then();
    }

    /**
     * Handle navigation help state responses
     */
    private Mono<Void> handleNavigationHelpState(String phoneNumber, String userInput) {
        switch (userInput.toLowerCase()) {
            case "get_directions":
                return startDirectionsFlow(phoneNumber);
                
            case "optimize_route":
                return startRouteOptimization(phoneNumber);
                
            case "alternative_routes":
                return showAlternativeRoutes(phoneNumber);
                
            case "real_time_nav":
                return startRealTimeNavigation(phoneNumber);
                
            default:
                return showMainMenu(phoneNumber);
        }
    }

    /**
     * Handle location search state
     */
    private Mono<Void> handleLocationSearchState(String phoneNumber, String userInput) {
        // Simulate location search (in real implementation, integrate with Google Maps API)
        String responseMessage = "🔍 Searching for: " + userInput + "\n\n" +
                                "Found these locations:\n" +
                                "1. " + userInput + " - Main Location\n" +
                                "2. " + userInput + " - Secondary Location\n" +
                                "3. Nearby alternatives\n\n" +
                                "Would you like directions to any of these?";
        
        String[] buttonIds = {"get_directions_1", "get_directions_2", "search_again"};
        String[] buttonTitles = {"Directions to #1", "Directions to #2", "Search Again"};
        
        return whatsAppService.sendButtonMessage(phoneNumber, responseMessage, buttonIds, buttonTitles)
                .then();
    }

    /**
     * Start directions flow
     */
    private Mono<Void> startDirectionsFlow(String phoneNumber) {
        sessionService.updateSessionState(phoneNumber, "ROUTE_PLANNING");
        
        String directionsMessage = "🗺️ Getting Directions\n\n" +
                                 "Please provide:\n" +
                                 "1. Your starting location (or 'current location')\n" +
                                 "2. Your destination\n\n" +
                                 "Format: 'From [Start] to [Destination]'\n" +
                                 "Example: 'From current location to Times Square, NYC'";
        
        return whatsAppService.sendTextMessage(phoneNumber, directionsMessage)
                .then();
    }

    /**
     * Show main menu
     */
    private Mono<Void> showMainMenu(String phoneNumber) {
        sessionService.updateSessionState(phoneNumber, "MAIN_MENU");
        
        String menuMessage = "🏠 Main Menu\n\n" +
                           "How can I assist you with navigation today?";
        
        String[] optionIds = {"navigation_help", "find_location", "traffic_info", "settings", "help"};
        String[] optionTitles = {"Navigation Help", "Find Location", "Traffic Info", "Settings", "Help & Support"};
        String[] optionDescriptions = {
            "Get directions and route help",
            "Search for places and addresses",
            "Check traffic conditions",
            "Manage your preferences",
            "Get help using this service"
        };
        
        return whatsAppService.sendListMessage(phoneNumber, menuMessage, "Choose Option", 
                                             "Navigation Services", optionIds, optionTitles, optionDescriptions)
                .then();
    }

    /**
     * Handle other states (placeholder implementations)
     */
    private Mono<Void> handleRoutePlanningState(String phoneNumber, String userInput) {
        String routeMessage = "🚗 Route Planning\n\n" +
                            "Processing your route request: " + userInput + "\n\n" +
                            "Estimated time: 25 minutes\n" +
                            "Distance: 15.2 km\n" +
                            "Traffic: Light traffic\n\n" +
                            "Would you like to start navigation?";
        
        String[] buttonIds = {"start_navigation", "alternative_route", "main_menu"};
        String[] buttonTitles = {"Start Navigation", "See Alternatives", "Main Menu"};
        
        return whatsAppService.sendButtonMessage(phoneNumber, routeMessage, buttonIds, buttonTitles)
                .then();
    }

    private Mono<Void> handleTrafficInfoState(String phoneNumber, String userInput) {
        String trafficResponse = "🚦 Traffic Update\n\n" +
                               "Current traffic conditions:\n" +
                               "• Main routes: Light traffic\n" +
                               "• Highway 101: Moderate delays\n" +
                               "• Downtown area: Heavy traffic\n\n" +
                               "Recommended: Use alternative routes";
        
        return whatsAppService.sendTextMessage(phoneNumber, trafficResponse)
                .then(showMainMenu(phoneNumber));
    }

    private Mono<Void> handleSettingsState(String phoneNumber, String userInput) {
        return showMainMenu(phoneNumber);
    }

    private Mono<Void> startRouteOptimization(String phoneNumber) {
        return whatsAppService.sendTextMessage(phoneNumber, "🔄 Route optimization feature coming soon!")
                .then(showMainMenu(phoneNumber));
    }

    private Mono<Void> showAlternativeRoutes(String phoneNumber) {
        return whatsAppService.sendTextMessage(phoneNumber, "🛣️ Alternative routes feature coming soon!")
                .then(showMainMenu(phoneNumber));
    }

    private Mono<Void> startRealTimeNavigation(String phoneNumber) {
        return whatsAppService.sendTextMessage(phoneNumber, "📱 Real-time navigation feature coming soon!")
                .then(showMainMenu(phoneNumber));
    }
}
