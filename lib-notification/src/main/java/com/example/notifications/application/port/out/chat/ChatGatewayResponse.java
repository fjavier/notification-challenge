package com.example.notifications.application.port.out.chat;

public record ChatGatewayResponse(String messageId, String status, String errorMessage) {
}
