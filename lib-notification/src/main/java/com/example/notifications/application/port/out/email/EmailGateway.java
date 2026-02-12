package com.example.notifications.application.port.out.email;

public interface EmailGateway {
    EmailGatewayResponse send(EmailMessage emailMessage);
}
