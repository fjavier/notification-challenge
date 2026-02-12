package com.example.notifications.application.service;

import com.example.notifications.application.port.in.Notifier;
import com.example.notifications.domain.result.NotificationResult;
import com.example.notifications.domain.model.SmsNotification;
import com.example.notifications.application.port.out.sms.SmsGateway;
import com.example.notifications.application.port.out.sms.SmsGatewayResponse;
import com.example.notifications.application.port.out.sms.SmsMessage;
import com.example.notifications.application.port.out.template.TemplateEngine;

public class SmsNotifier implements Notifier<SmsNotification> {

    private final SmsGateway gateway;
    private final TemplateEngine engine;

    public SmsNotifier(SmsGateway gateway, TemplateEngine engine) {
        this.gateway = gateway;
        this.engine = engine;
    }

    @Override
    public NotificationResult send(SmsNotification notification) {
        String message = notification.messageTemplate();

        if(notification.variables()!=null && !notification.variables().isEmpty()) {
            message = engine.render(notification.messageTemplate(), notification.variables());
        }

        SmsMessage smsMessage  = new SmsMessage(
                notification.sender(),notification.recipient(), message
        );
        SmsGatewayResponse response = gateway.send(smsMessage);
        if (response!= null && response.error() != null)
        {
            return NotificationResult.failure(response.error());
        }
        return NotificationResult.success();
    }
}
