package com.restro.service;

import com.restro.entity.OtpPurpose;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp, OtpPurpose purpose);

}
