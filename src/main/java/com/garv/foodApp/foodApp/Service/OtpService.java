package com.garv.foodApp.foodApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.from}")
    private String fromEmail;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OtpService.class);

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtpEmail(String to, String otp, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            logger.info("Sending OTP from: {} to: {}", fromEmail, to);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText("Your OTP is: " + otp + ". It is valid for 5 minutes.");
            mailSender.send(message);
            logger.info("OTP email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}
