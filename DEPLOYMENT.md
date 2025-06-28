# WhatsApp Chatbot - Deployment Guide

## 📋 Pre-Deployment Checklist

### ✅ Required Accounts & Services

- [ ] WhatsApp Business Account
- [ ] Facebook Developer Account
- [ ] Firebase Project
- [ ] Render Account (for deployment)
- [ ] Domain name (optional, for custom webhook URL)

### ✅ Configuration Files

- [ ] `application.properties` updated with your credentials
- [ ] `firebase-service-account.json` placed in resources folder
- [ ] Environment variables documented

## 🚀 Step-by-Step Deployment

### 1. WhatsApp Business API Setup

#### A. Create Facebook App

1. Go to [Facebook for Developers](https://developers.facebook.com)
2. Click "Create App" → "Business" → Enter app name
3. Add "WhatsApp Business API" product
4. Complete Business Verification (may take 1-3 days)

#### B. Get API Credentials

```
Phone Number ID: Found in WhatsApp → Getting Started
Access Token: Found in WhatsApp → Getting Started → Temporary Token
Business Account ID: Found in WhatsApp → Getting Started
```

#### C. Configure Webhook (do this after deployment)

```
Webhook URL: https://your-app.onrender.com/api/webhook
Verify Token: Create a secure random string (save this!)
```

### 2. Firebase Setup

#### A. Create Project

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Create a project"
3. Enable Google Analytics (optional)
4. Wait for project creation

#### B. Enable Firestore

1. Go to Firestore Database
2. Click "Create database"
3. Choose "Start in production mode"
4. Select location (choose closest to your users)

#### C. Create Service Account

1. Go to Project Settings → Service Accounts
2. Click "Generate new private key"
3. Download the JSON file
4. Rename to `firebase-service-account.json`
5. Place in `src/main/resources/`

### 3. Local Testing Setup

#### A. Configure Application

Update `src/main/resources/application.properties`:

```properties
# WhatsApp Configuration
whatsapp.api.phone-number-id=YOUR_PHONE_NUMBER_ID
whatsapp.api.access-token=YOUR_ACCESS_TOKEN
whatsapp.webhook.verify-token=YOUR_VERIFY_TOKEN

# Firebase Configuration
firebase.config.path=src/main/resources/firebase-service-account.json
firebase.database.url=https://YOUR_PROJECT_ID-default-rtdb.firebaseio.com
```

#### B. Test Locally

```bash
# Run the application
mvn spring-boot:run

# Install ngrok for webhook testing
# Download from https://ngrok.com
ngrok http 8080

# Use the HTTPS URL for webhook configuration
```

#### C. Test Endpoints

```bash
# Health check
curl http://localhost:8080/api/health

# Send test message
curl -X POST "http://localhost:8080/api/send-message" \
  -d "to=YOUR_PHONE_NUMBER&message=Test message"

# Dashboard
Open: http://localhost:8080
```

### 4. Production Deployment on Render

#### A. Prepare Repository

1. Push your code to GitHub
2. Ensure `.gitignore` excludes sensitive files
3. Verify `pom.xml` is correctly configured

#### B. Create Render Service

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New" → "Web Service"
3. Connect GitHub repository
4. Configure service:
   - **Name**: whatsapp-chatbot
   - **Region**: Choose closest to your users
   - **Branch**: main
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/whatsapp-chatbot-1.0.0.jar`

#### C. Configure Environment Variables

In Render dashboard, add these environment variables:

```
# WhatsApp API
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id
WHATSAPP_ACCESS_TOKEN=your_access_token
WEBHOOK_VERIFY_TOKEN=your_verify_token

# Database (Render provides PostgreSQL)
DATABASE_HOST=your_postgres_host
DATABASE_PORT=5432
DATABASE_NAME=your_database_name
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# Firebase
FIREBASE_CONFIG_PATH=src/main/resources/firebase-service-account.json
FIREBASE_DATABASE_URL=https://your-project.firebaseio.com
```

#### D. Add Database

1. In Render dashboard, create PostgreSQL database
2. Copy connection details to environment variables
3. Update `application.properties` for production:

```properties
# Production database configuration
spring.datasource.url=jdbc:postgresql://${DATABASE_HOST}:${DATABASE_PORT}/${DATABASE_NAME}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

#### E. Deploy

1. Click "Deploy" in Render dashboard
2. Monitor build logs
3. Wait for deployment to complete
4. Test the deployed URL

### 5. Configure WhatsApp Webhook

#### A. Set Webhook URL

1. Go to Facebook Developer Console
2. Navigate to WhatsApp → Configuration
3. Set webhook URL: `https://your-app.onrender.com/api/webhook`
4. Set verify token (same as environment variable)
5. Subscribe to these fields:
   - `messages`
   - `message_deliveries`
   - `message_reads`
   - `message_reactions`

#### B. Test Webhook

```bash
# Test webhook verification
curl "https://your-app.onrender.com/api/webhook?hub.mode=subscribe&hub.challenge=test&hub.verify_token=YOUR_VERIFY_TOKEN"

# Should return "test"
```

### 6. Testing & Verification

#### A. Send Test Message

1. Send "Hi" to your WhatsApp Business number
2. Should receive welcome menu with buttons
3. Try different menu options
4. Check database for message records

#### B. Dashboard Testing

1. Open: `https://your-app.onrender.com`
2. Use dashboard to send test messages
3. Check statistics and message history
4. Verify all features work

#### C. Monitor Logs

```bash
# In Render dashboard, check logs for:
# - Successful webhook requests
# - Message processing
# - Any errors or warnings
```

## 🔧 Production Configuration

### Database Migration

Update `pom.xml` to include PostgreSQL driver:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Environment-Specific Properties

Create `application-prod.properties`:

```properties
# Production-specific configurations
logging.level.com.whatsapp.chatbot=INFO
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=validate

# Security
server.error.include-stacktrace=never
server.error.include-message=never
```

## 🛡️ Security Considerations

### 1. API Security

- Use HTTPS for all endpoints
- Validate webhook signatures
- Implement rate limiting
- Sanitize user inputs

### 2. Environment Variables

- Never commit secrets to git
- Use Render's environment variable encryption
- Rotate tokens regularly

### 3. Database Security

- Use connection pooling
- Enable SSL for database connections
- Regular backups

## 📊 Monitoring & Maintenance

### 1. Health Monitoring

- Set up Render health checks
- Monitor webhook endpoint
- Track response times

### 2. Logging

- Monitor application logs
- Set up alerts for errors
- Track user interactions

### 3. Analytics

- Use Firebase Analytics
- Monitor message volumes
- Track user engagement

## 🚨 Troubleshooting

### Common Issues

#### Webhook Verification Failed

```
Status: 403 Forbidden
Solution: Check verify token matches exactly
```

#### Firebase Permission Denied

```
Error: Permission denied
Solution: Verify service account has proper roles
```

#### Database Connection Error

```
Error: Connection refused
Solution: Check database credentials and network
```

#### WhatsApp API Rate Limits

```
Error: (#4) Application request limit reached
Solution: Implement exponential backoff retry
```

### Debug Commands

```bash
# Check webhook health
curl https://your-app.onrender.com/api/webhook/health

# Test message sending
curl -X POST "https://your-app.onrender.com/api/send-message" \
  -d "to=YOUR_NUMBER&message=Debug test"

# Check application health
curl https://your-app.onrender.com/api/health
```

## 📈 Scaling Considerations

### 1. Horizontal Scaling

- Use multiple Render instances
- Implement load balancing
- Use Redis for session storage

### 2. Database Optimization

- Add database indexes
- Implement connection pooling
- Consider read replicas

### 3. Caching

- Implement Redis caching
- Cache frequently accessed data
- Use CDN for static assets

## 🔄 Maintenance Tasks

### Weekly

- [ ] Check application logs
- [ ] Monitor message volumes
- [ ] Verify webhook health

### Monthly

- [ ] Review error rates
- [ ] Update dependencies
- [ ] Check Firebase usage
- [ ] Rotate access tokens

### Quarterly

- [ ] Security audit
- [ ] Performance optimization
- [ ] Feature usage analysis
- [ ] Cost optimization

## 📞 Support Resources

### Documentation

- [WhatsApp Business API Docs](https://developers.facebook.com/docs/whatsapp)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Render Documentation](https://render.com/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

### Community

- WhatsApp Business API Community
- Firebase Community
- Stack Overflow
- GitHub Issues

---

**🎉 Congratulations! Your WhatsApp Chatbot is now live and ready to help users with navigation assistance!**
