package com.example.notifications.domain.model;

import java.util.Map;

public record SmsNotification(
        String recipient,
        String messageTemplate,
        Map<String, Object> variables
) implements Notification {}
