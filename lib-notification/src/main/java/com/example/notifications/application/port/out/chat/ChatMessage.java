package com.example.notifications.application.port.out.chat;

public record ChatMessage(
        String message,
        String destination
) {
}
