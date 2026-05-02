package com.restro.service.impl;

import com.restro.entity.OtpPurpose;
import com.restro.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp, OtpPurpose purpose) {

        String subject;
        String body;

        switch (purpose) {

            case REGISTRATION:
                subject = "Welcome to Restro - Email Verification OTP";
                body =
                        "Hello User,\n\n" +
                                "Thank you for registering with Restro.\n\n" +
                                "Your OTP for email verification is: " + otp + "\n" +
                                "This OTP is valid for 10 minutes.\n\n" +
                                "Please verify your email to complete your registration.\n\n" +
                                "If you did not create this account, please ignore this email.\n\n" +
                                "Thanks,\nRestro Team";
                break;

            case FORGOT_PASSWORD:
                subject = "Restro Password Reset OTP";
                body =
                        "Hello User,\n\n" +
                                "Your OTP to reset your password is: " + otp + "\n" +
                                "This OTP is valid for 10 minutes.\n\n" +
                                "If you did not request this, please ignore this email.\n\n" +
                                "Regards,\nRestro Team";
                break;

            case PASSWORD_RESET_SUCCESS:
                subject = "Restro Password Reset Successful";
                body =
                        "Hello User,\n\n" +
                                "Your password has been reset successfully.\n\n" +
                                "If this was not you, please contact support immediately.\n\n" +
                                "Regards,\nRestro Team";
                break;

            default:
                throw new IllegalArgumentException("Invalid OTP purpose");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

        } catch (Exception ex) {
            System.err.println("Failed to send OTP email to " + toEmail);
            ex.printStackTrace();
        }
    }
}
