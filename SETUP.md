# WhatsApp Chatbot Project - Setup Guide

## Quick Start Instructions

### 1. Environment Setup

Before running the application, you need to configure the following:

#### WhatsApp Business API Setup

1. **Create WhatsApp Business Account**

   - Go to [Facebook for Developers](https://developers.facebook.com)
   - Create a new app and add WhatsApp Business API
   - Get your Phone Number ID and Access Token

2. **Configure Webhook**
   - Set webhook URL to: `https://your-domain.com/api/webhook`
   - Set verify token (use any secure string)

#### Firebase Setup

1. **Create Firebase Project**

   - Go to [Firebase Console](https://console.firebase.google.com)
   - Create a new project
   - Enable Firestore Database

2. **Download Service Account Key**
   - Go to Project Settings > Service Accounts
   - Generate new private key
   - Save as `firebase-service-account.json` in `src/main/resources/`

### 2. Configuration

Update `src/main/resources/application.properties`:

```properties
# WhatsApp Business API Configuration
whatsapp.api.phone-number-id=YOUR_PHONE_NUMBER_ID
whatsapp.api.access-token=YOUR_ACCESS_TOKEN
whatsapp.webhook.verify-token=YOUR_VERIFY_TOKEN

# Firebase Configuration
firebase.config.path=src/main/resources/firebase-service-account.json
firebase.database.url=https://YOUR_PROJECT_ID.firebaseio.com
```

### 3. Running the Application

#### Development Mode

```bash
# Clone the repository
git clone <repository-url>
cd whatsapp-chatbot-project

# Run with Maven
mvn spring-boot:run

# Or compile and run JAR
mvn clean package
java -jar target/whatsapp-chatbot-1.0.0.jar
```

#### Using ngrok for local testing

```bash
# Install ngrok
# Download from https://ngrok.com/

# Expose local server
ngrok http 8080

# Use the HTTPS URL as your webhook endpoint
```

### 4. Testing

#### API Endpoints

- Health Check: `GET http://localhost:8080/api/health`
- Send Message: `POST http://localhost:8080/api/send-message`
- View Messages: `GET http://localhost:8080/api/messages`
- Database Console: `http://localhost:8080/api/h2-console`

#### Test Message Flow

1. Send "Hi" to your WhatsApp Business number
2. Bot should respond with welcome menu
3. Try different menu options
4. Check database for stored messages

### 5. Production Deployment on Render

#### Prepare for Production

1. **Update Database Configuration**

   ```properties
   # Production MySQL Database
   spring.datasource.url=jdbc:mysql://${DATABASE_HOST}/${DATABASE_NAME}
   spring.datasource.username=${DATABASE_USERNAME}
   spring.datasource.password=${DATABASE_PASSWORD}
   ```

2. **Environment Variables**
   Set in Render dashboard:
   - `WHATSAPP_PHONE_NUMBER_ID`
   - `WHATSAPP_ACCESS_TOKEN`
   - `WEBHOOK_VERIFY_TOKEN`
   - `DATABASE_HOST`
   - `DATABASE_NAME`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`

#### Deploy to Render

1. Connect GitHub repository to Render
2. Create new Web Service
3. Build Command: `mvn clean package -DskipTests`
4. Start Command: `java -jar target/whatsapp-chatbot-1.0.0.jar`
5. Set environment variables
6. Deploy

### 6. Webhook Configuration

After deployment, update WhatsApp webhook:

- URL: `https://your-app.onrender.com/api/webhook`
- Verify Token: Same as configured in environment

### 7. Features Overview

#### Navigation Assistant Features

- **Get Directions**: Step-by-step route guidance
- **Location Search**: Find places and addresses
- **Traffic Information**: Real-time traffic updates
- **Route Optimization**: Find fastest routes

#### Interactive Elements

- **Button Messages**: Quick action buttons
- **List Messages**: Structured menu options
- **Text Input**: Natural language processing

#### Session Management

- **State Tracking**: Remembers conversation context
- **User Preferences**: Stores user settings
- **Navigation History**: Tracks user journey

### 8. Troubleshooting

#### Common Issues

**Webhook Verification Failed**

- Check verify token matches in both WhatsApp and application
- Ensure webhook URL is accessible and returns 200 status
- Use HTTPS in production

**Firebase Connection Error**

- Verify service account JSON file exists and is valid
- Check Firebase project ID and database URL
- Ensure Firestore is enabled in Firebase console

**WhatsApp API Errors**

- Validate access token and phone number ID
- Check message format compliance with WhatsApp API
- Verify business account status

**Database Connection Issues**

- Check database credentials in production
- Ensure MySQL service is running
- Verify network connectivity

#### Debug Commands

```bash
# Check application logs
mvn spring-boot:run

# Test database connection
curl http://localhost:8080/api/health

# Send test message
curl -X POST "http://localhost:8080/api/send-message" \
  -d "to=1234567890&message=Test message"

# Check webhook status
curl http://localhost:8080/api/webhook/health
```

### 9. Development Tips

#### Code Structure

- Controllers handle HTTP requests
- Services contain business logic
- Repositories manage data access
- DTOs define API contracts
- Models represent database entities

#### Adding New Features

1. Define new conversation states in `ChatbotService`
2. Add corresponding handlers
3. Update session management if needed
4. Add tests for new functionality
5. Update API documentation

#### Database Schema

```sql
-- Messages table
CREATE TABLE whatsapp_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(255) UNIQUE NOT NULL,
    from_number VARCHAR(255) NOT NULL,
    to_number VARCHAR(255) NOT NULL,
    message_text TEXT,
    message_type VARCHAR(255),
    timestamp DATETIME NOT NULL,
    status VARCHAR(255),
    direction VARCHAR(255)
);

-- User sessions table
CREATE TABLE user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(255) UNIQUE NOT NULL,
    current_state VARCHAR(255),
    navigation_path TEXT,
    user_preferences TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    session_active BOOLEAN DEFAULT TRUE
);
```

### 10. Support

For help and support:

- Check this setup guide
- Review the main README.md
- Check application logs for errors
- Create an issue in the repository
- Review WhatsApp Business API documentation

---

**Happy Coding! 🚀**
