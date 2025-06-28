# Mock Mode Setup Guide

## Overview

Mock mode allows you to test the WhatsApp Chatbot application without setting up real WhatsApp Business API credentials. All API calls will return successful mock responses instead of making actual HTTP requests to WhatsApp.

## Current Configuration

Mock mode is **ENABLED** by default in the current setup. You can see this in `application.properties`:

```properties
whatsapp.api.mock-mode=${WHATSAPP_MOCK_MODE:true}
```

## How Mock Mode Works

### 1. Text Messages

When you send a text message via `/api/send-message`, you'll get a response like:

```json
{
  "success": true,
  "message": "Message sent successfully",
  "response": {
    "to": "1234567890",
    "type": "text",
    "status": "SENT",
    "message": "Your test message",
    "timestamp": "2025-06-28T16:20:48.973"
  }
}
```

### 2. Button Messages

When you send a button message via `/api/send-button-message`, you'll get:

```json
{
  "success": true,
  "message": "Button message sent successfully",
  "response": {
    "to": "1234567890",
    "type": "interactive",
    "status": "SENT",
    "message": "Choose an option",
    "timestamp": "2025-06-28T16:20:58.388"
  }
}
```

## Testing the Application

### Using the Web Dashboard

1. Open http://localhost:8080 in your browser
2. Use the "Send Test Message" form
3. Enter any phone number (e.g., 1234567890)
4. Enter a test message
5. Click "Send Message"
6. You should see a successful response

### Using API Directly

```bash
# Test text message
curl -X POST "http://localhost:8080/api/send-message" \
  -d "to=1234567890&message=Hello from mock mode"

# Test button message
curl -X POST "http://localhost:8080/api/send-button-message" \
  -d "to=1234567890&bodyText=Choose option&buttonIds=btn1,btn2&buttonTitles=Yes,No"
```

## Enabling/Disabling Mock Mode

### Option 1: Environment Variable

Set the environment variable before starting the application:

```bash
# Enable mock mode
export WHATSAPP_MOCK_MODE=true

# Disable mock mode (use real API)
export WHATSAPP_MOCK_MODE=false
```

### Option 2: Update application.properties

Change the default value in `src/main/resources/application.properties`:

```properties
# Enable mock mode
whatsapp.api.mock-mode=true

# Disable mock mode
whatsapp.api.mock-mode=false
```

## When to Use Mock Mode

✅ **Use Mock Mode When:**

- Testing application functionality
- Developing new features
- Running automated tests
- Don't have WhatsApp Business API credentials yet
- Want to avoid API rate limits during development

❌ **Disable Mock Mode When:**

- Ready to send real WhatsApp messages
- Have valid WhatsApp Business API credentials
- Testing end-to-end message delivery
- Running in production

## Switching to Real WhatsApp API

When you're ready to use the real WhatsApp Business API:

1. **Get your credentials** (see WHATSAPP_SETUP.md)
2. **Set environment variables:**
   ```bash
   export WHATSAPP_MOCK_MODE=false
   export WHATSAPP_PHONE_NUMBER_ID=your_actual_phone_number_id
   export WHATSAPP_ACCESS_TOKEN=your_actual_access_token
   export WEBHOOK_VERIFY_TOKEN=your_webhook_verify_token
   ```
3. **Restart the application**
4. **Test with a real phone number**

## Troubleshooting

### Mock Mode Not Working

- Check application logs for "Mock Mode: true"
- Verify `whatsapp.api.mock-mode=true` in configuration
- Restart the application after changes

### Still Getting 401 Errors

- Ensure mock mode is enabled
- Check for typos in configuration
- Verify application restart after changes

### Want to Test Real API

- Set `WHATSAPP_MOCK_MODE=false`
- Configure real WhatsApp API credentials
- Restart application
