package com.example.notifications.domain.model;

import java.util.Map;

public record EmailNotification(
        String recipient,
        String subjectTemplate,
        String bodyTemplate,
        Map<String, Object> variables
) implements Notification {}
