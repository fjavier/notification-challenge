package com.example.notifications.domain.model;

import java.util.Map;

public record PushNotification(
        String recipient,
        String deviceToken,
        String titleTemplate,
        String bodyTemplate,
        Map<String, Object> variables,
        Map<String, String> data
) implements Notification {}
