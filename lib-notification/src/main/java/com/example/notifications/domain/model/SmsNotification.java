package com.example.notifications.domain.model;

import java.util.Map;

public record SmsNotification(
        String sender,
        String recipient,
        String messageTemplate,
        Map<String, Object> variables
) implements Notification {
    public SmsNotification {
        if (sender == null || sender.isBlank()) {
            throw new IllegalArgumentException("Sender is required");
        }

        if ( !sender.matches("^\\+?[1-9]\\d{7,14}$")) {
            throw new IllegalArgumentException("Invalid recipient phone number");
        }

        if (recipient == null || !recipient.matches("^\\+?[1-9]\\d{7,14}$")) {
            throw new IllegalArgumentException("Invalid recipient phone number");
        }

        if (messageTemplate == null || messageTemplate.isBlank()) {
            throw new IllegalArgumentException("messageTemplate is required");
        }
    }
}
