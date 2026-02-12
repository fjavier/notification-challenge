package com.example.notifications.application.port.in;

import com.example.notifications.domain.model.Notification;
import com.example.notifications.domain.result.NotificationResult;

public interface Notifier<T extends Notification> {
    NotificationResult send(T notification);
}
