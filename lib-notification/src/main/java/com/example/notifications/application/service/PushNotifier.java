package com.example.notifications.application.service;

import com.example.notifications.application.port.in.Notifier;
import com.example.notifications.domain.result.NotificationResult;
import com.example.notifications.domain.model.PushNotification;
import com.example.notifications.application.port.out.push.PushGateway;
import com.example.notifications.application.port.out.push.PushGatewayResponse;
import com.example.notifications.application.port.out.push.PushMessage;
import com.example.notifications.application.port.out.template.TemplateEngine;

public class PushNotifier implements Notifier<PushNotification> {

    private final PushGateway gateway;
    private final TemplateEngine templateEngine;

    public PushNotifier(PushGateway gateway, TemplateEngine engine) {
        this.gateway = gateway;
        this.templateEngine = engine;
    }

    @Override
    public NotificationResult send(PushNotification notificationMessage) {
        String title = templateEngine.render(notificationMessage.titleTemplate(), notificationMessage.variables());
        String body = templateEngine.render(notificationMessage.bodyTemplate(), notificationMessage.variables());

        PushMessage message = new PushMessage(
                notificationMessage.deviceToken(),
                title,
                body,
                notificationMessage.data()
        );
        PushGatewayResponse response = gateway.send(message);

        if (response.errors() != null && !response.errors().isEmpty()) {
            return NotificationResult.failure(response.errors());
        }

        return NotificationResult.success();
    }
}