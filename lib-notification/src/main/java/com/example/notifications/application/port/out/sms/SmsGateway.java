package com.example.notifications.application.port.out.sms;

public interface SmsGateway {
    SmsGatewayResponse send(SmsMessage smsMessage);
}
