# WhatsApp Chatbot Project

A comprehensive WhatsApp chatbot solution built with Java Spring Boot, integrating with WhatsApp Business API, Firebase, and designed for deployment on Render.

## 🚀 Features

- **WhatsApp Business API Integration**: Send and receive messages, interactive buttons, and lists
- **Navigation Assistance**: Help users with directions, route planning, and traffic information
- **Session Management**: Track user conversations and maintain state
- **Firebase Integration**: Store analytics and user data in Firestore
- **RESTful APIs**: Management and testing endpoints
- **Database Support**: H2 (development) and MySQL (production)
- **Comprehensive Testing**: Unit and integration tests
- **Production Ready**: Configured for Render deployment

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: H2 (dev), MySQL (prod)
- **Cloud Services**: Firebase Firestore
- **API Integration**: WhatsApp Business API
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven
- **Deployment**: Render

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/whatsapp/chatbot/
│   │   ├── WhatsAppChatbotApplication.java     # Main application class
│   │   ├── config/                             # Configuration classes
│   │   │   ├── CorsConfig.java
│   │   │   └── WebClientConfig.java
│   │   ├── controller/                         # REST controllers
│   │   │   ├── ChatbotApiController.java       # Management API
│   │   │   └── WhatsAppWebhookController.java  # Webhook handler
│   │   ├── dto/                                # Data Transfer Objects
│   │   │   ├── WebhookRequest.java
│   │   │   └── WhatsAppOutboundMessage.java
│   │   ├── model/                              # JPA entities
│   │   │   ├── UserSession.java
│   │   │   └── WhatsAppMessage.java
│   │   ├── repository/                         # Data repositories
│   │   │   ├── UserSessionRepository.java
│   │   │   └── WhatsAppMessageRepository.java
│   │   └── service/                            # Business logic
│   │       ├── ChatbotService.java             # Conversation logic
│   │       ├── FirebaseService.java            # Firebase integration
│   │       ├── UserSessionService.java         # Session management
│   │       └── WhatsAppService.java            # WhatsApp API
│   └── resources/
│       └── application.properties              # Configuration
└── test/                                       # Unit tests
```

## 🔧 Setup & Installation

### Prerequisites

- Java 17+
- Maven 3.6+
- WhatsApp Business Account
- Firebase Project
- Render Account (for deployment)

### Local Development Setup

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd whatsapp-chatbot-project
   ```

2. **Configure WhatsApp Business API**

   - Create a WhatsApp Business Account
   - Get your Phone Number ID and Access Token
   - Set up webhook URL

3. **Configure Firebase**

   - Create a Firebase project
   - Download service account key
   - Place the JSON file in `src/main/resources/`

4. **Update Configuration**
   Edit `src/main/resources/application.properties`:

   ```properties
   # WhatsApp Configuration
   whatsapp.api.phone-number-id=YOUR_PHONE_NUMBER_ID
   whatsapp.api.access-token=YOUR_ACCESS_TOKEN
   whatsapp.webhook.verify-token=YOUR_VERIFY_TOKEN

   # Firebase Configuration
   firebase.config.path=src/main/resources/firebase-service-account.json
   firebase.database.url=https://your-project.firebaseio.com
   ```

5. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

6. **Test the webhook**
   - The application runs on `http://localhost:8080`
   - Webhook endpoint: `http://localhost:8080/api/webhook`
   - Use ngrok for local testing: `ngrok http 8080`

## 📱 API Endpoints

### Webhook Endpoints

- `GET /api/webhook` - Webhook verification
- `POST /api/webhook` - Receive WhatsApp messages

### Management API

- `GET /api/health` - Health check
- `POST /api/send-message` - Send text message
- `POST /api/send-button-message` - Send button message
- `GET /api/messages/{phoneNumber}` - Get message history
- `GET /api/sessions` - Get active sessions
- `GET /api/stats` - Get chatbot statistics

### Testing Endpoints

- `POST /api/test-chatbot` - Test chatbot functionality
- `POST /api/session/{phoneNumber}/reset` - Reset user session

## 🤖 Chatbot Features

### Navigation Assistance

- **Step-by-step directions**: Get detailed route instructions
- **Route optimization**: Find the fastest routes
- **Alternative routes**: Explore different path options
- **Real-time navigation**: Live navigation assistance

### Interactive Elements

- **Button Messages**: Quick action buttons
- **List Messages**: Structured option selections
- **Text Input**: Natural language processing

### Session Management

- **State Tracking**: Maintain conversation context
- **User Preferences**: Remember user settings
- **Navigation History**: Track user journey

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Manual Testing

1. Send a message to your WhatsApp Business number
2. Use the management API endpoints
3. Check the H2 console: `http://localhost:8080/api/h2-console`

## 🚀 Deployment on Render

### 1. Prepare for Production

Update `application.properties` for production:

```properties
# Use MySQL for production
spring.datasource.url=jdbc:mysql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}

# Environment variables
whatsapp.api.phone-number-id=${WHATSAPP_PHONE_NUMBER_ID}
whatsapp.api.access-token=${WHATSAPP_ACCESS_TOKEN}
whatsapp.webhook.verify-token=${WEBHOOK_VERIFY_TOKEN}
```

### 2. Create Render Service

1. Connect your GitHub repository
2. Choose "Web Service"
3. Configure build and start commands:
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/whatsapp-chatbot-1.0.0.jar`

### 3. Set Environment Variables

In Render dashboard, add:

- `WHATSAPP_PHONE_NUMBER_ID`
- `WHATSAPP_ACCESS_TOKEN`
- `WEBHOOK_VERIFY_TOKEN`
- `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`
- `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `FIREBASE_CONFIG_PATH`

### 4. Configure Webhook

Update WhatsApp webhook URL to your Render service:
`https://your-app.onrender.com/api/webhook`

## 📊 Monitoring & Analytics

### Database Tables

- `whatsapp_messages`: Message history and status
- `user_sessions`: User conversation state and preferences

### Firebase Collections

- `user_interactions`: User activity analytics
- `navigation_requests`: Navigation usage data
- `user_preferences`: User settings and preferences
- `analytics`: General analytics events

### API Statistics

- Total messages sent/received
- Active user sessions
- Popular navigation requests
- Error rates and performance metrics

## 🔒 Security Features

- Webhook token verification
- CORS configuration
- Input validation
- Error handling
- Rate limiting (configurable)

## 🐛 Troubleshooting

### Common Issues

1. **Webhook Verification Failed**

   - Check verify token configuration
   - Ensure HTTPS URL for production

2. **Firebase Connection Issues**

   - Verify service account key path
   - Check Firebase project configuration

3. **WhatsApp API Errors**
   - Validate access token
   - Check phone number ID
   - Verify message format

### Logs

Check application logs for detailed error information:

```bash
# Local development
mvn spring-boot:run

# Production (Render)
# Check logs in Render dashboard
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:

- Create an issue in the repository
- Check the troubleshooting section
- Review the API documentation

## 🚧 Future Enhancements

- [ ] Google Maps API integration for real locations
- [ ] Voice message support
- [ ] Multi-language support
- [ ] Advanced analytics dashboard
- [ ] AI-powered conversation improvements
- [ ] Location sharing features
- [ ] Route sharing and collaboration

---

**Built with ❤️ using Java Spring Boot**
