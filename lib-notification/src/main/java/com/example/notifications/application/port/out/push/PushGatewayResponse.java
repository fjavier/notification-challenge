package com.example.notifications.application.port.out.push;

public record PushGatewayResponse(
        String messageId,
        String status,
        String errors
) {}
