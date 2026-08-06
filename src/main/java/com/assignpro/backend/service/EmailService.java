package com.assignpro.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService — gracefully optional.
 * If app.email.enabled=false (the default), all email methods are no-ops.
 * Registration and password-reset flows succeed even when SMTP is not
 * configured.
 * Set app.email.enabled=true and the MAIL_* env vars on Render to enable real
 * emails.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        if (!emailEnabled) {
            log.info("Email disabled — skipping verification email to {}", toEmail);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("AssignPro - Email Verification");
            message.setText("To verify your AssignPro account, click the link below:\n\n"
                    + "https://assignpro-backend.onrender.com/api/auth/verify?token=" + token
                    + "\n\nIf you did not register, please ignore this email.");
            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            // Do NOT rethrow — email failure must never block registration
        }
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        if (!emailEnabled) {
            log.info("Email disabled — skipping password-reset email to {}", toEmail);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("AssignPro - Password Reset");
            message.setText("To reset your AssignPro password, use the token below:\n\n"
                    + "Token: " + token
                    + "\n\nIf you did not request this, please ignore this email.");
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password-reset email to {}: {}", toEmail, e.getMessage());
            // Do NOT rethrow — email failure must never block password reset
        }
    }
}
