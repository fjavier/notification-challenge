package com.example.notifications.domain.model;

import java.util.Map;

public record EmailNotification(
        String sender,
        String recipient,
        String subjectTemplate,
        String bodyTemplate,
        Map<String, Object> variables
) implements Notification {
    public EmailNotification {
        if (sender == null || sender.isBlank()) {
            throw new IllegalArgumentException("Sender is required");
        }

        if ( !sender.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid recipient email");
        }

        if (recipient == null || !recipient.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid recipient email");
        }

        if (subjectTemplate == null || subjectTemplate.isBlank()) {
            throw new IllegalArgumentException("Subject is required");
        }

        if (bodyTemplate == null || bodyTemplate.isBlank()) {
            throw new IllegalArgumentException("Body is required");
        }
    }
}
