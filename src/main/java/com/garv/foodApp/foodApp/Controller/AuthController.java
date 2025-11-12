package com.garv.foodApp.foodApp.Controller;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Entity.AuthRequest;
import com.garv.foodApp.foodApp.Service.AuthService;
import com.garv.foodApp.foodApp.Utlity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    /**
     * Legacy endpoint - kept for backward compatibility
     */
    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest, Principal principal) {
       try{
           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
           );

           return jwtTokenUtil.generateToken(authRequest.getUsername());// return the token
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    /**
     * Register a new user
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), false));
        }
    }

    /**
     * Verify OTP sent to email during registration
     * POST /auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpRequest request) {
        try {
            AuthResponse response = authService.verifyOtp(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), false));
        }
    }

    /**
     * Resend OTP to email
     * POST /auth/resend-otp
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<AuthResponse> resendOtp(@RequestBody ForgotPasswordRequest request) {
        try {
            AuthResponse response = authService.resendOtp(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), false));
        }
    }

    /**
     * Login with email and password - sends OTP
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Verify login OTP and get access token
     * POST /auth/verify-login-otp
     */
    @PostMapping("/verify-login-otp")
    public ResponseEntity<LoginResponse> verifyLoginOtp(@RequestBody OtpRequest request) {
        try {
            LoginResponse response = authService.verifyLoginOtp(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Request password reset OTP
     * POST /auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            AuthResponse response = authService.forgotPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), false));
        }
    }

    /**
     * Reset password with OTP
     * POST /auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            AuthResponse response = authService.resetPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), false));
        }
    }

    /**
     * Refresh access token using refresh token
     * POST /auth/refresh-token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Logout - invalidate refresh token
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("User not authenticated", false));
            }
            AuthResponse response = authService.logout(principal.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), false));
        }
    }
}

