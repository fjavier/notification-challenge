package com.example.notifications.application.service;

import com.example.notifications.application.port.in.Notifier;
import com.example.notifications.domain.result.NotificationResult;
import com.example.notifications.domain.model.EmailNotification;
import com.example.notifications.application.port.out.email.EmailGateway;
import com.example.notifications.application.port.out.email.EmailGatewayResponse;
import com.example.notifications.application.port.out.email.EmailMessage;
import com.example.notifications.application.port.out.template.TemplateEngine;

public class EmailNotifier implements Notifier<EmailNotification> {

    private final TemplateEngine engine;
    private final EmailGateway gateway;

    public EmailNotifier(EmailGateway emailGateway,
                         TemplateEngine engine) {
        this.engine = engine;
        this.gateway = emailGateway;
    }

    @Override
    public NotificationResult send(EmailNotification email) {

        String subject = email.subjectTemplate();
        String body = email.bodyTemplate();

        if(email.variables()!=null && !email.variables().isEmpty()) {
            subject = engine.render(email.subjectTemplate(), email.variables());
            body = engine.render(email.bodyTemplate(), email.variables());
        }

        EmailMessage message = new EmailMessage(email.sender(),
          email.recipient(), subject, body
        );

        EmailGatewayResponse response = gateway.send(message);
        if (response.errorMessage() != null) {
            return NotificationResult.failure(response.errorMessage());
        }
        return NotificationResult.success();
    }
}
