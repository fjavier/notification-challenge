package com.example.notifications.infraestructure.email.mailgun;

import com.example.notifications.application.port.out.email.EmailGateway;
import com.example.notifications.application.port.out.email.EmailGatewayResponse;
import com.example.notifications.application.port.out.email.EmailMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailgunEmailProvider implements EmailGateway {

    private final MailgunConfig config;

    public MailgunEmailProvider(MailgunConfig config) {
        this.config = config;
    }

    @Override
    public EmailGatewayResponse send(EmailMessage message) {

        // Mapear modelo interno → modelo Mailgun
        MailgunRequest request = new MailgunRequest(
                message.from(),
                message.to(),
                message.subject(),
                message.body(),
                config.domain()
        );

        // Simular envío
        MailgunResponse response = simulatedSend(request);

        // Traducir respuesta proveedor → modelo unificado
        if (response.errors() == null || response.errors().isEmpty()) {
            log.info("Correo enviado a: {}, messageId: {}", message.to(), response.id());
            return new EmailGatewayResponse(
                    response.id(),
                    "SENT",
                    null
            );
        }

        return new EmailGatewayResponse(
                null,
                "FAILED",
                response.errors()
        );
    }

    protected MailgunResponse simulatedSend(MailgunRequest request) {

        // Simulación realista
        return new MailgunResponse(
                "mailgun-" + System.currentTimeMillis(),
                "Queued. Thank you.",
                null
        );
    }
}
