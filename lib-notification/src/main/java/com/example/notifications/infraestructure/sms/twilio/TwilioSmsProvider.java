package com.example.notifications.infraestructure.sms.twilio;

import com.example.notifications.application.port.out.sms.SmsGateway;
import com.example.notifications.application.port.out.sms.SmsGatewayResponse;
import com.example.notifications.application.port.out.sms.SmsMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TwilioSmsProvider implements SmsGateway {

    private final TwilioConfig config;

    public TwilioSmsProvider(TwilioConfig config) {
        this.config = config;
    }

    @Override
    public SmsGatewayResponse send(SmsMessage message) {

        TwilioRequest request = new TwilioRequest(
                config.fromNumber(),
                message.to(),
                message.body()
        );

        TwilioResponse response = simulatedSend(request);
        if ("queued".equalsIgnoreCase(response.status())) {
            log.info("Mensaje enviado via SMS Provider a: {}", message.to());
            return new SmsGatewayResponse(
                    response.sid(),
                    response.status(),
                    null
            );
        }

        return new SmsGatewayResponse(
                null,
                response.status(),
                response.errors()
        );
    }

    protected TwilioResponse simulatedSend(TwilioRequest request) {
        return new TwilioResponse(
                "SM" + System.currentTimeMillis(),
                "queued",
                null
        );
    }
}
