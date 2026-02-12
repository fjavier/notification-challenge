package com.example.notifications.application.port.out.sms;

public record SmsMessage(
        String from,
        String to,
        String body
) {}
