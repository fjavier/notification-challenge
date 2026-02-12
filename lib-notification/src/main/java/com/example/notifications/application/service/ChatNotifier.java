package com.example.notifications.application.service;

import com.example.notifications.application.port.in.Notifier;
import com.example.notifications.application.port.out.chat.ChatGateway;
import com.example.notifications.application.port.out.chat.ChatGatewayResponse;
import com.example.notifications.application.port.out.chat.ChatMessage;
import com.example.notifications.application.port.out.template.TemplateEngine;
import com.example.notifications.domain.model.ChatNotification;
import com.example.notifications.domain.result.NotificationResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatNotifier implements Notifier<ChatNotification> {

    private final TemplateEngine engine;
    private final ChatGateway gateway;

    public ChatNotifier(ChatGateway chatGateway,
                        TemplateEngine engine) {
        this.engine = engine;
        this.gateway = chatGateway;
    }

    @Override
    public NotificationResult send(ChatNotification notification) {
        String body = notification.messageTemplate();

        if(notification.variables()!=null && !notification.variables().isEmpty()) {
            body = engine.render(notification.messageTemplate(), notification.variables());
        }

        ChatMessage message = new ChatMessage(body,notification.recipient());

        ChatGatewayResponse response = gateway.send(message);
        if (response.errorMessage() != null) {
            return NotificationResult.failure(response.errorMessage());
        }

        log.info("Chat enviado correctamente a: {}", notification.recipient());
        return NotificationResult.success();
    }
}
