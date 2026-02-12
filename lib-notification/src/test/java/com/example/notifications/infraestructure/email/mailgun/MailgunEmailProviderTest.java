package com.example.notifications.infraestructure.email.mailgun;

import com.example.notifications.application.port.out.email.EmailGatewayResponse;
import com.example.notifications.application.port.out.email.EmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class MailgunEmailProviderTest {
    private MailgunEmailProvider provider;

    @BeforeEach
    void setUp() {
        MailgunConfig config = new MailgunConfig("fake-api-key", "fake-domain","baseUrl");
        provider = new MailgunEmailProvider(config);
    }

    @Test
    void shouldSendEmailSuccessfully() {
        EmailMessage message = new EmailMessage(
                "from@test.com",
                "to@test.com",
                "Subject",
                "Body"
        );

        EmailGatewayResponse response = provider.send(message);

        assertNotNull(response);
        assertNotNull(response.messageId());
        assertEquals("SENT", response.status());
        assertNull(response.errorMessage());
    }

    @Test
    void shouldReturnErrorWhenMailgunFails() {
        // Arrange
        MailgunEmailProvider spyProvider = Mockito.spy(provider);

        // 👇 Firma correcta del record
        MailgunResponse errorResponse = new MailgunResponse(
                null,
                "Unauthorized", "error"
        );

        Mockito.doReturn(errorResponse)
                .when(spyProvider)
                .simulatedSend(Mockito.any());

        EmailMessage message = new EmailMessage(
                "from@test.com",
                "to@test.com",
                "Subject",
                "Body"
        );

        // Act
        EmailGatewayResponse response = spyProvider.send(message);

        // Assert
        assertNotNull(response);
        assertNull(response.messageId());
        assertEquals("FAILED", response.status());
        assertEquals("error", response.errorMessage());
    }

    @Test
    void shouldGenerateDifferentMessageIds() throws InterruptedException {
        EmailMessage message = new EmailMessage(
                "from@test.com",
                "to@test.com",
                "Subject",
                "Body"
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