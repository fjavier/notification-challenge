package com.example.notifications.domain.model;

import java.util.Map;

public record ChatNotification(
        String recipient,       // channelId, chatId, roomId
        String messageTemplate,
        Map<String, Object> variables
) implements Notification {
    public ChatNotification {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("recipient is required");
        }
        if (messageTemplate == null || messageTemplate.isBlank()) {
            throw new IllegalArgumentException("messageTemplate is required");
        }
    }
}
