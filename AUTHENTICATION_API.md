# Authentication API Documentation

This document describes the authentication endpoints available in the Food App API.

## Base URL
All endpoints are relative to: `/api/v1/auth`

## Authentication Flow

### 1. User Registration
Users must register and verify their email before they can login.

### 2. Email Verification
After registration, an OTP is sent to the user's email. This must be verified to enable the account.

### 3. Login
Once verified, users can login with their email and password to receive access and refresh tokens.

### 4. Token Refresh
When the access token expires, use the refresh token to get a new access token.

### 5. Logout
Invalidates the refresh token.

## Endpoints

### Register User
Creates a new user account and sends an OTP to the provided email for verification.

**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
  "userName": "johndoe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123!",
  "role": "CUSTOMER"
}
```

**Roles:**
- `CUSTOMER` (default)
- `ADMIN`
- `RESTAURANT_OWNER`
- `DELIVERY_PERSON`

**Response:**
```json
{
  "message": "Registration successful. Please verify your email with the OTP sent.",
  "success": true
}
```

**Status Codes:**
- `200 OK` - Registration successful
- `400 Bad Request` - Email already registered or validation error

---

### Verify OTP
Verifies the OTP sent to the user's email during registration.

**Endpoint:** `POST /auth/verify-otp`

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "otp": "123456"
}
```

**Response:**
```json
{
  "message": "Email verified successfully. You can now login.",
  "success": true
}
```

**Status Codes:**
- `200 OK` - OTP verified successfully
- `400 Bad Request` - Invalid or expired OTP

**Notes:**
- OTP is valid for 5 minutes
- Account is enabled after successful verification

---

### Resend OTP
Resends the OTP to the user's email.

**Endpoint:** `POST /auth/resend-otp`

**Request Body:**
```json
{
  "email": "john.doe@example.com"
}
```

**Response:**
```json
{
  "message": "OTP sent successfully to your email.",
  "success": true
}
```

**Status Codes:**
- `200 OK` - OTP sent successfully
- `400 Bad Request` - User not found or email sending failed

---

### Login
Authenticates a user and returns access and refresh tokens.

**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "userName": "johndoe",
  "email": "john.doe@example.com",
  "role": "ROLE_CUSTOMER",
  "message": "Login successful"
}
```

**Status Codes:**
- `200 OK` - Login successful
- `400 Bad Request` - Invalid credentials or account not verified

**Notes:**
- Access token is valid for 1 hour
- Refresh token is valid for 7 days
- Account must be verified (OTP) before login

---

### Forgot Password
Initiates the password reset process by sending an OTP to the user's email.

**Endpoint:** `POST /auth/forgot-password`

**Request Body:**
```json
{
  "email": "john.doe@example.com"
}
```

**Response:**
```json
{
  "message": "Password reset OTP sent to your email.",
  "success": true
}
```

**Status Codes:**
- `200 OK` - OTP sent successfully
- `400 Bad Request` - User not found or email sending failed

---

### Reset Password
Resets the user's password using the OTP received via email.

**Endpoint:** `POST /auth/reset-password`

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "otp": "123456",
  "newPassword": "NewSecurePassword123!"
}
```

**Response:**
```json
{
  "message": "Password reset successful. You can now login with your new password.",
  "success": true
}
```

**Status Codes:**
- `200 OK` - Password reset successful
- `400 Bad Request` - Invalid or expired OTP

**Notes:**
- OTP is valid for 5 minutes
- Password is encrypted before storage

---

### Refresh Token
Generates a new access token using a valid refresh token.

**Endpoint:** `POST /auth/refresh-token`

**Request Body:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "userName": "johndoe",
  "email": "john.doe@example.com",
  "role": "ROLE_CUSTOMER",
  "message": "Token refreshed successfully"
}
```

**Status Codes:**
- `200 OK` - Token refreshed successfully
- `400 Bad Request` - Invalid or expired refresh token

**Notes:**
- Refresh token must not be expired (7 days validity)
- Same refresh token is returned (not rotated)

---

### Logout
Invalidates the user's refresh token.

**Endpoint:** `POST /auth/logout`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:** None required (user identified from JWT token)

**Response:**
```json
{
  "message": "Logged out successfully",
  "success": true
}
```

**Status Codes:**
- `200 OK` - Logout successful
- `400 Bad Request` - User not authenticated

**Notes:**
- Requires valid access token in Authorization header
- Deletes the refresh token from the database

---

### Legacy Authenticate (Deprecated)
Legacy endpoint for backward compatibility. Use `/auth/login` instead.

**Endpoint:** `POST /auth/authenticate`

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "SecurePassword123!"
}
```

**Response:** Returns only the JWT token as a plain string.

---

## Gmail Configuration

To enable OTP functionality via Gmail, configure the following in `application.properties`:

```properties
# Gmail SMTP Configuration for OTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

### Generating Google App Password

1. Enable 2-Step Verification on your Google account
2. Go to Google Account Settings → Security → 2-Step Verification → App passwords
3. Select "Mail" as the app and your device
4. Copy the generated 16-character password
5. Use this password in `spring.mail.password` property

**Note:** Never commit your actual credentials to version control. Use environment variables or secure configuration management.

## Security Features

1. **Password Encryption**: Passwords are encrypted using BCrypt before storage
2. **OTP Expiration**: OTPs expire after 5 minutes
3. **Account Verification**: Users must verify email before login
4. **Refresh Token**: Long-lived tokens for seamless authentication
5. **JWT**: Stateless authentication using JSON Web Tokens
6. **Account Locking**: Support for account locking mechanism (future enhancement)

## Error Handling

All endpoints return consistent error responses:

```json
{
  "message": "Error description",
  "success": false
}
```

Common error messages:
- "Email already registered"
- "Username already taken"
- "Invalid credentials"
- "Account not verified. Please verify your email with OTP."
- "Invalid OTP"
- "OTP expired. Please request a new one."
- "User not found"
- "Failed to send OTP email"
- "Refresh token expired. Please login again."
- "Invalid refresh token"

## Example Flow

### Complete Registration and Login Flow

1. **Register**
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "userName": "johndoe",
       "email": "john.doe@example.com",
       "password": "SecurePassword123!",
       "role": "CUSTOMER"
     }'
   ```

2. **Verify OTP** (check email for OTP)
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/verify-otp \
     -H "Content-Type: application/json" \
     -d '{
       "email": "john.doe@example.com",
       "otp": "123456"
     }'
   ```

3. **Login**
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "john.doe@example.com",
       "password": "SecurePassword123!"
     }'
   ```

4. **Use Access Token** (in subsequent requests)
   ```bash
   curl -X GET http://localhost:3001/api/v1/restaurants \
     -H "Authorization: Bearer <access_token>"
   ```

5. **Refresh Token** (when access token expires)
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/refresh-token \
     -H "Content-Type: application/json" \
     -d '{
       "refreshToken": "<refresh_token>"
     }'
   ```

6. **Logout**
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/logout \
     -H "Authorization: Bearer <access_token>"
   ```

### Password Reset Flow

1. **Request Reset**
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/forgot-password \
     -H "Content-Type: application/json" \
     -d '{
       "email": "john.doe@example.com"
     }'
   ```

2. **Reset Password** (check email for OTP)
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/reset-password \
     -H "Content-Type: application/json" \
     -d '{
       "email": "john.doe@example.com",
       "otp": "123456",
       "newPassword": "NewSecurePassword123!"
     }'
   ```

3. **Login with New Password**
   ```bash
   curl -X POST http://localhost:3001/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "john.doe@example.com",
       "password": "NewSecurePassword123!"
     }'
   ```
