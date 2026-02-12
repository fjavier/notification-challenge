package com.example.notifications.application.port.out.chat;

public interface ChatGateway {
    ChatGatewayResponse send(ChatMessage message);
}
