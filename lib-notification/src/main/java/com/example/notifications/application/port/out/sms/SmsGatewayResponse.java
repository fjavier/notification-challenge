package com.example.notifications.application.port.out.sms;

public record SmsGatewayResponse(
        String messageId,
        String status,
        String error
) {}
