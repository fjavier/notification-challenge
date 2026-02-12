package com.example.notifications.infraestructure.email.sendgrid;

import com.example.notifications.application.port.out.email.EmailGateway;
import com.example.notifications.application.port.out.email.EmailGatewayResponse;
import com.example.notifications.application.port.out.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendGridEmailProvider implements EmailGateway {

    private final SendGridConfig apiConfig;

    public SendGridEmailProvider(SendGridConfig config) {
        apiConfig = config;
    }

    @Override
    public EmailGatewayResponse send(EmailMessage message) {
        //Mapea Modelo Interno
        SendGridRequest request = new SendGridRequest(
                message.from(),
                message.to(),
                message.subject(),
                message.body()
        );

        //Simulacion llamada a Sendgrid
        SendGridResponse response = simulatedSend(request);

        //Traduciendo respuesta
        if ("accepted".equalsIgnoreCase(response.status())) {
            log.info("Correo enviado a: {}, messageId: {}", message.to(), response.messageId());
            return new EmailGatewayResponse(
                    response.messageId(), response.status(), null
            );
        }

        return new EmailGatewayResponse(null, response.status(), response.errors());
    }

    protected SendGridResponse simulatedSend(SendGridRequest request) {
        log.info("Enviando correo a: {}", request.to());
        return new SendGridResponse(
                "msg-"+System.currentTimeMillis(),
                "CREATED", "accepted", null
        );
    }
}
