package com.example.notifications.application.port.out.email;

public record EmailGatewayResponse(String messageId, String status, String errorMessage) {
}
