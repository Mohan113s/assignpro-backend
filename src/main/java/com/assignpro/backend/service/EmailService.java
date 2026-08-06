package com.assignpro.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("AssignPro - Email Verification");
        // Update URL properly if moving to production
        message.setText("To verify your AssignPro account, please click the link below:\n\n"
                + "https://assignpro-backend.onrender.com/api/auth/verify?token=" + token
                + "\n\nIf you did not register for this, please ignore this email.");
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("AssignPro - Password Reset");
        message.setText("To reset your AssignPro password, please use the token below:\n\n"
                + "Token: " + token
                + "\n\nIf you did not request this, please ignore this email.");
        mailSender.send(message);
    }
}
