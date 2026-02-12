package com.example.notifications.infraestructure.sms.twilio;

public record TwilioConfig(
        String accountSid,
        String authToken,
        String fromNumber
) {

    public TwilioConfig {
        if (accountSid == null || accountSid.isBlank()) {
            throw new IllegalArgumentException("Account SID is required");
        }
        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("Auth token is required");
        }
        if (fromNumber == null || fromNumber.isBlank()) {
            throw new IllegalArgumentException("From number is required");
        }
    }
}
