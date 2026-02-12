package com.example.notifications.infraestructure.chat.slack;

public record SlackResponse(
        boolean success,
        String rawResponse,
        String errorMessage
){
}
