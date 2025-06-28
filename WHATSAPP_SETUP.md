# WhatsApp Business API Setup Guide

## Prerequisites

- Facebook Developer Account
- WhatsApp Business Account
- Valid phone number for verification

## Step 1: Create Facebook Developer Account

1. Go to [Facebook for Developers](https://developers.facebook.com/)
2. Create a developer account if you don't have one
3. Create a new App and select "Business" as the type

## Step 2: Add WhatsApp Business API

1. In your Facebook App dashboard, add the "WhatsApp Business Platform" product
2. Complete the setup wizard
3. Verify your business and phone number

## Step 3: Get Your Credentials

After setup, you'll need these values:

### Required Credentials:

- **Phone Number ID**: Found in WhatsApp > API Setup
- **Access Token**: Found in WhatsApp > API Setup (temporary) or create permanent token
- **Webhook Verify Token**: You create this yourself (any secure string)

### Example Values (replace with your actual values):

```
WHATSAPP_PHONE_NUMBER_ID=123456789012345
WHATSAPP_ACCESS_TOKEN=EAAP...your_access_token
WEBHOOK_VERIFY_TOKEN=your_secure_webhook_token
```

## Step 4: Configure Environment Variables

### Option A: Environment Variables (Recommended for production)

Set these in your system environment:

```bash
export WHATSAPP_PHONE_NUMBER_ID=your_actual_phone_number_id
export WHATSAPP_ACCESS_TOKEN=your_actual_access_token
export WEBHOOK_VERIFY_TOKEN=your_webhook_verify_token
```

### Option B: Update application.properties (for development)

Update the file with your actual values:

```properties
whatsapp.api.phone-number-id=your_actual_phone_number_id
whatsapp.api.access-token=your_actual_access_token
whatsapp.webhook.verify-token=your_webhook_verify_token
```

## Step 5: Test Configuration

1. Restart the application
2. Use the dashboard to send a test message
3. Check that you receive the message on WhatsApp

## Important Security Notes

- Never commit access tokens to version control
- Use environment variables in production
- Rotate tokens regularly
- Use webhook verify tokens that are hard to guess

## Webhook Setup (Optional)

To receive messages, set up webhook URL:

- URL: `https://your-domain.com/webhook`
- Verify Token: Use the same token from your configuration
- Subscribe to: messages, message_status

## Testing Without Real API

If you want to test the application without setting up real WhatsApp API:

- Use the mock mode (see MOCK_SETUP.md)
- All endpoints will work but no actual messages will be sent
