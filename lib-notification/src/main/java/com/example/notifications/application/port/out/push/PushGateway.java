package com.example.notifications.application.port.out.push;

public interface PushGateway {
    PushGatewayResponse send(PushMessage message);
}
