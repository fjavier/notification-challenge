package com.example.notifications.infraestructure.push.firebase;

import com.example.notifications.application.port.out.push.PushGateway;
import com.example.notifications.application.port.out.push.PushGatewayResponse;
import com.example.notifications.application.port.out.push.PushMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirebasePushProvider implements PushGateway {

    private final FirebaseConfig config;

    public FirebasePushProvider(FirebaseConfig config) {
        this.config = config;
    }

    @Override
    public PushGatewayResponse send(PushMessage message) {

        FirebaseRequest request = new FirebaseRequest(
                message.token(),
                message.title(),
                message.body(),
                message.data()
        );

        FirebaseResponse response = simulatedSend(request);

        if ("success".equalsIgnoreCase(response.status())) {
            log.info("Mensaje enviado via Push Provider, messageId: {}", response.messageId());
            return new PushGatewayResponse(
                    response.messageId(),
                    response.status(),
                    null
            );
        }

        return new PushGatewayResponse(
                null,
                response.status(),
                response.errors()
        );
    }

    private FirebaseResponse simulatedSend(FirebaseRequest request) {

        return new FirebaseResponse(
                "firebase-" + System.currentTimeMillis(),
                "success",
                null
        );
    }
}
