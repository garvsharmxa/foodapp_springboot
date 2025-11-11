# Gmail Setup Guide for OTP Verification

This guide explains how to set up Gmail for sending OTP emails in the Food App authentication system.

## Prerequisites

1. A Gmail account
2. 2-Step Verification enabled on your Google account

## Step-by-Step Setup

### 1. Enable 2-Step Verification

1. Go to your [Google Account](https://myaccount.google.com/)
2. Navigate to **Security** in the left sidebar
3. Under "Signing in to Google", click on **2-Step Verification**
4. Follow the prompts to enable 2-Step Verification if not already enabled

### 2. Generate App Password

1. Once 2-Step Verification is enabled, go back to the Security page
2. Under "Signing in to Google", click on **App passwords**
   - If you don't see this option, make sure 2-Step Verification is enabled
3. You may need to sign in again for security
4. At the bottom, select:
   - **Select app**: Choose "Mail" or "Other (Custom name)"
   - **Select device**: Choose your device or "Other (Custom name)"
   - If using "Other", give it a name like "Food App Backend"
5. Click **Generate**
6. Google will display a 16-character password (format: xxxx xxxx xxxx xxxx)
7. **Copy this password immediately** - you won't be able to see it again

### 3. Configure Application Properties

Open your `application.properties` file and update the Gmail configuration:

```properties
# Gmail SMTP Configuration for OTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

Replace:
- `your-email@gmail.com` with your actual Gmail address
- `xxxx xxxx xxxx xxxx` with the 16-character app password you generated (spaces are optional)

### 4. Environment Variables (Recommended for Production)

For security reasons, **never commit credentials** to version control. Instead, use environment variables:

#### Option 1: Using System Environment Variables

```properties
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

Set environment variables:
```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=xxxxxxxxxxxx
```

#### Option 2: Using application-prod.properties

Create a separate `application-prod.properties` file (add to `.gitignore`):

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=xxxxxxxxxxxx
```

Run with:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Option 3: Using Docker Environment Variables

In `docker-compose.yml`:
```yaml
environment:
  - MAIL_USERNAME=your-email@gmail.com
  - MAIL_PASSWORD=xxxxxxxxxxxx
```

### 5. Testing the Configuration

Start your application and try registering a new user:

```bash
curl -X POST http://localhost:3001/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "email": "test@example.com",
    "password": "Test123!",
    "role": "CUSTOMER"
  }'
```

Check the specified email inbox for the OTP.

## Troubleshooting

### Issue: "535-5.7.8 Username and Password not accepted"

**Solution:**
1. Verify that 2-Step Verification is enabled
2. Make sure you're using an App Password, not your regular Gmail password
3. Regenerate the App Password if needed
4. Check for typos in username and password

### Issue: "Failed to send email"

**Solution:**
1. Check your internet connection
2. Verify SMTP host and port are correct
3. Ensure `starttls.enable=true` is set
4. Check if your firewall is blocking port 587

### Issue: "Authentication failed"

**Solution:**
1. Make sure you're using the correct Gmail address
2. The App Password should be exactly 16 characters
3. Try regenerating a new App Password
4. Restart your application after updating configuration

### Issue: Email goes to spam

**Solution:**
1. Add a proper "From" name in the email configuration
2. Consider using a custom domain email (e.g., Google Workspace)
3. Implement SPF/DKIM records if using a custom domain
4. Ask recipients to mark your emails as "Not Spam"

### Issue: "Less secure app access"

**Note:** Google no longer supports "Less secure app access" as of May 30, 2022. You **must** use App Passwords with 2-Step Verification.

## Security Best Practices

1. **Never commit credentials**: Always use environment variables or secure vaults
2. **Rotate passwords regularly**: Generate new App Passwords periodically
3. **Limit access**: Use separate App Passwords for different applications
4. **Monitor usage**: Check your Google Account activity regularly
5. **Revoke unused passwords**: Delete App Passwords that are no longer in use

## Revoking App Passwords

If you need to revoke access:

1. Go to your [Google Account](https://myaccount.google.com/)
2. Navigate to **Security** → **App passwords**
3. Click on the trash icon next to the App Password you want to revoke
4. Confirm the deletion

## Alternative Email Providers

While this guide focuses on Gmail, the application can work with other SMTP providers:

### SendGrid
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=YOUR_SENDGRID_API_KEY
```

### AWS SES
```properties
spring.mail.host=email-smtp.us-east-1.amazonaws.com
spring.mail.port=587
spring.mail.username=YOUR_SMTP_USERNAME
spring.mail.password=YOUR_SMTP_PASSWORD
```

### Mailgun
```properties
spring.mail.host=smtp.mailgun.org
spring.mail.port=587
spring.mail.username=YOUR_MAILGUN_SMTP_USERNAME
spring.mail.password=YOUR_MAILGUN_SMTP_PASSWORD
```

## Production Recommendations

For production environments:

1. **Use a dedicated email service** (SendGrid, AWS SES, Mailgun) instead of Gmail
2. **Implement rate limiting** to prevent email abuse
3. **Add email templates** for better formatting
4. **Include unsubscribe links** for transactional emails
5. **Monitor email delivery rates** and bounce rates
6. **Set up email logging** for audit trails
7. **Implement retry logic** for failed email sends

## Email Rate Limits

Gmail has sending limits:
- **Free Gmail**: 500 emails per day
- **Google Workspace**: 2,000 emails per day

For high-volume applications, consider dedicated email services with higher limits.

## Contact

If you encounter issues not covered in this guide, please:
1. Check the [Spring Mail documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#mail)
2. Review [Google's App Password help](https://support.google.com/accounts/answer/185833)
3. Open an issue in the project repository

---

**Last Updated:** 2025-11-11
