package com.example.notifications.infraestructure.sms.twilio;

public record TwilioRequest(
        String from,
        String to,
        String body
) {}
