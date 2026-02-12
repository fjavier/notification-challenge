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
    public NotificationResult send(SmsNotification n) {
        String message = engine.render(n.messageTemplate(), n.variables());
        SmsMessage smsMessage  = new SmsMessage(
                "SYSTEM",n.recipient(), message
        );
        SmsGatewayResponse response = gateway.send(smsMessage);
        if (response!= null && response.error() != null)
        {
            return NotificationResult.failure(response.error());
        }
        return NotificationResult.success();
    }
}
