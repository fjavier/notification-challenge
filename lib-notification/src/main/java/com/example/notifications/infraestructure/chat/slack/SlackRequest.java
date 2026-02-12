package com.example.notifications.infraestructure.chat.slack;

public record SlackRequest(
        String text,
        String channel
) {
}
