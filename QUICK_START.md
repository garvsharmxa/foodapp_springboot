# Quick Start Guide - Authentication System

This guide will help you get started with the new authentication features in 5 minutes.

## Prerequisites

- PostgreSQL database running
- Java 17 installed
- Maven installed
- Gmail account with App Password (see [GMAIL_SETUP.md](GMAIL_SETUP.md))

## Step 1: Configure Gmail (5 minutes)

1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Step Verification
3. Generate App Password for Mail
4. Copy the 16-character password

## Step 2: Update Configuration

Edit `src/main/resources/application.properties`:

```properties
# Update these lines with your values:
spring.mail.username=your-email@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx  # Your 16-char app password

# Update database credentials if needed:
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
```

## Step 3: Run the Application

```bash
# Build and run
./mvnw spring-boot:run
```

The application will start on `http://localhost:3001`

## Step 4: Test Authentication Flow

### Test 1: Register a User

```bash
curl -X POST http://localhost:3001/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "email": "your-test-email@gmail.com",
    "password": "Test123!",
    "role": "CUSTOMER"
  }'
```

**Expected Response:**
```json
{
  "message": "Registration successful. Please verify your email with the OTP sent.",
  "success": true
}
```

**Check your email** for the OTP (6-digit code).

### Test 2: Verify OTP

```bash
curl -X POST http://localhost:3001/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "otp": "123456"  # Replace with actual OTP from email
  }'
```

**Expected Response:**
```json
{
  "message": "Email verified successfully. You can now login.",
  "success": true
}
```

### Test 3: Login

```bash
curl -X POST http://localhost:3001/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "password": "Test123!"
  }'
```

**Expected Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "userName": "testuser",
  "email": "your-test-email@gmail.com",
  "role": "ROLE_CUSTOMER",
  "message": "Login successful"
}
```

**Save the `accessToken`** - you'll need it for authenticated requests.

### Test 4: Access Protected Endpoint

```bash
# Replace YOUR_ACCESS_TOKEN with the actual token from login
curl -X GET http://localhost:3001/api/v1/restaurants \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Test 5: Refresh Token (when access token expires)

```bash
# Replace YOUR_REFRESH_TOKEN with the actual refresh token from login
curl -X POST http://localhost:3001/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### Test 6: Logout

```bash
curl -X POST http://localhost:3001/api/v1/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Common Issues & Solutions

### Issue 1: Email not received
**Solution:** 
- Check spam folder
- Verify Gmail App Password is correct
- Wait 1-2 minutes for email delivery
- Use resend-otp endpoint if needed

### Issue 2: "Account not verified"
**Solution:**
- Must verify email with OTP before login
- Use verify-otp endpoint with OTP from email

### Issue 3: "OTP expired"
**Solution:**
- OTP is valid for only 5 minutes
- Request new OTP using resend-otp endpoint

### Issue 4: "Invalid credentials"
**Solution:**
- Check email and password are correct
- Password is case-sensitive
- Make sure account is verified

### Issue 5: Database connection failed
**Solution:**
- Ensure PostgreSQL is running
- Check database name, username, and password
- Create database if it doesn't exist: `createdb foodAppDB`

## Available Endpoints

All endpoints are under `/api/v1/auth`:

| Endpoint | Purpose | Authentication |
|----------|---------|----------------|
| POST /register | Register new user | No |
| POST /verify-otp | Verify email | No |
| POST /resend-otp | Resend OTP | No |
| POST /login | Login | No |
| POST /forgot-password | Request password reset | No |
| POST /reset-password | Reset password | No |
| POST /refresh-token | Refresh access token | No |
| POST /logout | Logout | Yes (Bearer token) |

## User Roles

When registering, you can specify one of these roles:
- `CUSTOMER` (default) - Regular users
- `ADMIN` - Administrators
- `RESTAURANT_OWNER` - Restaurant owners
- `DELIVERY_PERSON` - Delivery personnel

Example:
```json
{
  "userName": "adminuser",
  "email": "admin@example.com",
  "password": "Admin123!",
  "role": "ADMIN"
}
```

## Testing Password Reset

### Step 1: Request Reset
```bash
curl -X POST http://localhost:3001/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com"
  }'
```

### Step 2: Check Email for OTP

### Step 3: Reset Password
```bash
curl -X POST http://localhost:3001/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "otp": "123456",
    "newPassword": "NewTest123!"
  }'
```

### Step 4: Login with New Password
```bash
curl -X POST http://localhost:3001/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "password": "NewTest123!"
  }'
```

## Using Postman

Import this collection to test all endpoints:

1. Create new collection: "Food App Auth"
2. Add environment variables:
   - `BASE_URL`: `http://localhost:3001/api/v1`
   - `ACCESS_TOKEN`: (will be set after login)
   - `REFRESH_TOKEN`: (will be set after login)

3. Create requests for each endpoint using the examples above
4. Use `{{BASE_URL}}` and `{{ACCESS_TOKEN}}` variables

## Next Steps

1. ✅ Read [AUTHENTICATION_API.md](AUTHENTICATION_API.md) for complete API documentation
2. ✅ Read [GMAIL_SETUP.md](GMAIL_SETUP.md) for detailed Gmail configuration
3. ✅ Read [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for technical details
4. 🔄 Test all authentication flows with your own email
5. 🔄 Integrate with your frontend application
6. 🔄 Deploy to production with proper environment variables

## Security Reminders

⚠️ **Never commit sensitive data:**
- Don't commit Gmail password to git
- Don't commit database passwords
- Use environment variables in production
- Keep JWT secret key secure

✅ **Best practices:**
- Use strong passwords (min 8 chars, mixed case, numbers, symbols)
- Enable HTTPS in production
- Rotate App Passwords regularly
- Monitor failed login attempts
- Set up logging and alerts

## Production Deployment

Before going to production:

1. Update `application.properties` to use environment variables
2. Set up production email service (SendGrid, AWS SES, etc.)
3. Configure HTTPS/SSL
4. Enable rate limiting
5. Set up monitoring and alerts
6. Test all flows thoroughly
7. Create backup admin account

## Support

Need help?
- Check [AUTHENTICATION_API.md](AUTHENTICATION_API.md) for API details
- Check [GMAIL_SETUP.md](GMAIL_SETUP.md) for email setup
- Check application logs in console output
- Check [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for troubleshooting

## Success Indicators

You've successfully set up the authentication system when:
- ✅ User can register and receive OTP email
- ✅ User can verify email with OTP
- ✅ User can login and receive tokens
- ✅ User can access protected endpoints with access token
- ✅ User can refresh expired access token
- ✅ User can reset password with OTP
- ✅ User can logout successfully

Congratulations! 🎉 Your authentication system is ready!

---

**Quick Reference:**
- Base URL: `http://localhost:3001/api/v1`
- Auth Endpoints: `/auth/*`
- Access Token Validity: 1 hour
- Refresh Token Validity: 7 days
- OTP Validity: 5 minutes
