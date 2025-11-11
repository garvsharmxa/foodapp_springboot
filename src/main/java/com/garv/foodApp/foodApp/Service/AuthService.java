package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Entity.RefreshToken;
import com.garv.foodApp.foodApp.Entity.Users;
import com.garv.foodApp.foodApp.Repository.UserRepository;
import com.garv.foodApp.foodApp.Utlity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final long OTP_VALIDITY_MINUTES = 5;

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email already registered", false);
        }

        // Check if username already exists
        if (userRepository.existsByUserName(request.getUserName())) {
            return new AuthResponse("Username already taken", false);
        }

        // Create new user
        Users user = new Users();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? "ROLE_" + request.getRole() : "ROLE_CUSTOMER");
        user.setEnabled(false); // Account not enabled until OTP verification
        user.setAccountNonLocked(true);

        // Generate and send OTP
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());

        userRepository.save(user);

        // Send OTP email
        try {
            otpService.sendOtpEmail(user.getEmail(), otp, "Account Verification");
            return new AuthResponse("Registration successful. Please verify your email with the OTP sent.", true);
        } catch (Exception e) {
            return new AuthResponse("Registration successful but failed to send OTP email: " + e.getMessage(), true);
        }
    }

    public AuthResponse verifyOtp(OtpRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if OTP is valid
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            return new AuthResponse("Invalid OTP", false);
        }

        // Check if OTP is expired
        if (user.getOtpGeneratedTime() == null || 
            ChronoUnit.MINUTES.between(user.getOtpGeneratedTime(), LocalDateTime.now()) > OTP_VALIDITY_MINUTES) {
            return new AuthResponse("OTP expired. Please request a new one.", false);
        }

        // Enable user account
        user.setEnabled(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        return new AuthResponse("Email verified successfully. You can now login.", true);
    }

    public AuthResponse resendOtp(ForgotPasswordRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new OTP
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        // Send OTP email
        try {
            otpService.sendOtpEmail(user.getEmail(), otp, "OTP Resend");
            return new AuthResponse("OTP sent successfully to your email.", true);
        } catch (Exception e) {
            return new AuthResponse("Failed to send OTP: " + e.getMessage(), false);
        }
    }

    public LoginResponse login(LoginRequest request) {
        try {
            // Find user by email
            Users user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            // Generate tokens
            String accessToken = jwtUtil.generateToken(user.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            LoginResponse response = new LoginResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken.getToken());
            response.setUserName(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setMessage("Login successful");

            return response;

        } catch (DisabledException e) {
            throw new RuntimeException("Account not verified. Please verify your email with OTP.");
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        // Generate OTP
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        // Send OTP email
        try {
            otpService.sendOtpEmail(user.getEmail(), otp, "Password Reset");
            return new AuthResponse("Password reset OTP sent to your email.", true);
        } catch (Exception e) {
            return new AuthResponse("Failed to send OTP: " + e.getMessage(), false);
        }
    }

    public AuthResponse resetPassword(ResetPasswordRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify OTP
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            return new AuthResponse("Invalid OTP", false);
        }

        // Check if OTP is expired
        if (user.getOtpGeneratedTime() == null || 
            ChronoUnit.MINUTES.between(user.getOtpGeneratedTime(), LocalDateTime.now()) > OTP_VALIDITY_MINUTES) {
            return new AuthResponse("OTP expired. Please request a new one.", false);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        return new AuthResponse("Password reset successful. You can now login with your new password.", true);
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtUtil.generateToken(user.getUsername());
                    
                    LoginResponse response = new LoginResponse();
                    response.setAccessToken(accessToken);
                    response.setRefreshToken(request.getRefreshToken());
                    response.setUserName(user.getUsername());
                    response.setEmail(user.getEmail());
                    response.setRole(user.getRole());
                    response.setMessage("Token refreshed successfully");
                    
                    return response;
                })
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

    public AuthResponse logout(String username) {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        refreshTokenService.deleteByUserId(user.getId());
        
        return new AuthResponse("Logged out successfully", true);
    }
}
