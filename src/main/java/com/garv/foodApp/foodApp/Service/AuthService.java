package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.*;
import com.garv.foodApp.foodApp.Entity.RefreshToken;
import com.garv.foodApp.foodApp.Entity.Users;
import com.garv.foodApp.foodApp.Repository.UserRepository;
import com.garv.foodApp.foodApp.Utlity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        String email = normalize(request.getEmail());
        String username = normalize(request.getUsername());
        String password = normalize(request.getPassword());

        if (email == null) {
            return new AuthResponse("Email is required", false);
        }
        if (username == null) {
            return new AuthResponse("Username is required", false);
        }
        if (password == null || password.length() < 6) {
            return new AuthResponse("Password must be at least 6 characters", false);
        }

        Users user = userRepository.findByEmail(email).orElse(null);

        if (user != null && user.isEnabled()) {
            return new AuthResponse("Email already registered", false);
        }

        Users existingUsernameUser = userRepository.findByUsername(username).orElse(null);
        if (existingUsernameUser != null && (user == null || !existingUsernameUser.getId().equals(user.getId()))) {
            return new AuthResponse("Username already taken", false);
        }

        if (user == null) {
            user = new Users();
            user.setEmail(email);
            user.setEnabled(false);
            user.setAccountNonLocked(true);
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(resolveRole(request.getRole()));
        user.setEnabled(false);
        user.setAccountNonLocked(true);

        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());

        userRepository.save(user);

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

        user.setEnabled(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        AuthResponse response = new AuthResponse();
        response.setMessage("Registration complete.");
        response.setSuccess(true);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
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
        LoginResponse response = new LoginResponse();
        try {
            String email = normalize(request.getEmail());
            String password = normalize(request.getPassword());

            if (email == null || password == null) {
                throw new RuntimeException("Email and password are required");
            }

            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            if (!user.isEnabled()) {
                throw new DisabledException("Account not verified. Please verify your email with OTP.");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), password));

            String otp = otpService.generateOtp();
            user.setOtp(otp);
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);

            try {
                otpService.sendOtpEmail(user.getEmail(), otp, "Login Verification");
            } catch (Exception e) {
                throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
            }

            response.setSuccess(true);
            response.setMessage("OTP sent to your email. Please verify to complete login.");
            response.setUsername(user.getUsername());
            response.setRole(user.getRole().name());

            return response;

        } catch (DisabledException e) {
            throw new RuntimeException(e.getMessage());
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid credentials");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Login failed");
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
        response.setSuccess(true);
        response.setToken(accessToken);
        response.setRefreshToken(request.getRefreshToken());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setMessage("Token refreshed successfully");

        return response;
    })
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

    public LoginResponse verifyLoginOtp(OtpRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if OTP is valid
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // Check if OTP is expired
        if (user.getOtpGeneratedTime() == null ||
                ChronoUnit.MINUTES.between(user.getOtpGeneratedTime(), LocalDateTime.now()) > OTP_VALIDITY_MINUTES) {
            throw new RuntimeException("OTP expired. Please login again.");
        }

        // Clear OTP
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setMessage("Login successful");

        return response;
    }

    public AuthResponse logout(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.deleteByUserId(user.getId());

        return new AuthResponse("Logged out successfully", true);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Users.Role resolveRole(String role) {
        if (role != null) {
            try {
                return Users.Role.valueOf(role.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return Users.Role.USER;
    }
}
