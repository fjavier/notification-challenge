package com.example.notifications.domain.model;

public sealed interface Notification
        permits EmailNotification,
        SmsNotification,
        PushNotification,
        ChatNotification {

    String recipient();
}
