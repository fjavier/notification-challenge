package com.example.notifications.infraestructure.email.sendgrid;

import com.example.notifications.application.port.out.email.EmailGatewayResponse;
import com.example.notifications.application.port.out.email.EmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendGridEmailProviderTest {


    private SendGridEmailProvider provider;

    @BeforeEach
    void setUp() {
        SendGridConfig config = new SendGridConfig("fake-api-key","baseUrl");
        provider = new SendGridEmailProvider(config);
    }

    @Test
    void shouldSendEmailSuccessfully() {
        // Arrange
        EmailMessage message = new EmailMessage(
                "test@example.com",
                "destino@example.com",
                "Asunto prueba",
                "Contenido del correo"
        );

        // Act
        EmailGatewayResponse response = provider.send(message);

        // Assert
        assertNotNull(response);
        assertNotNull(response.messageId());
        assertEquals("accepted", response.status());
        assertNull(response.errorMessage());
    }

    @Test
    void shouldGenerateDifferentMessageIds() throws InterruptedException {
        EmailMessage message = new EmailMessage(
                "test@example.com",
                "destino@example.com",
                "Asunto prueba",
                "Contenido del correo"
        );

        EmailGatewayResponse response1 = provider.send(message);
        Thread.sleep(2);
        EmailGatewayResponse response2 = provider.send(message);
        assertNotEquals(response1.messageId(), response2.messageId());
    }

    @Test
    void shouldNotThrowExceptionWhenSending() {
        EmailMessage message = new EmailMessage(
                "from@test.com",
                "to@test.com",
                "Subject",
                "Body"
        );

        assertDoesNotThrow(() -> provider.send(message));
    }
}