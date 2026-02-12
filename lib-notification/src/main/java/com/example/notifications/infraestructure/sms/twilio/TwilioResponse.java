package com.example.notifications.infraestructure.sms.twilio;

public record TwilioResponse(
        String sid,
        String status,
        String errors
) {}