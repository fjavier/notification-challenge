package com.example.notifications.infraestructure.sms.twilio;

import com.example.notifications.application.port.out.sms.SmsGatewayResponse;
import com.example.notifications.application.port.out.sms.SmsMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwilioSmsProviderTest {

    private TwilioSmsProvider provider;

    @BeforeEach
    void setUp() {
        TwilioConfig config = new TwilioConfig("test-account-sid", "test-auth-token", "+1234567890");
        provider = new TwilioSmsProvider(config);
    }

    @Test
    void shouldSendSmsSuccessfully() {
        // Arrange
        SmsMessage message = new SmsMessage(
                null, // from is handled by config
                "+0987654321",
                "Test SMS Body");

        // Act
        SmsGatewayResponse response = provider.send(message);

        // Assert
        assertNotNull(response);
        assertNotNull(response.messageId());
        assertTrue(response.messageId().startsWith("SM"));
        assertEquals("queued", response.status());
        assertNull(response.error());
    }

    @Test
    void shouldGenerateDifferentSids() throws InterruptedException {
        // Arrange
        SmsMessage message = new SmsMessage(
                null,
                "+0987654321",
                "Test SMS Body");

        // Act
        SmsGatewayResponse response1 = provider.send(message);
        Thread.sleep(2); // Ensure timestamps are different
        SmsGatewayResponse response2 = provider.send(message);

        // Assert
        assertNotNull(response1.messageId());
        assertNotNull(response2.messageId());
        assertNotEquals(response1.messageId(), response2.messageId());
    }

    @Test
    void shouldNotThrowExceptionWhenSending() {
        // Arrange
        SmsMessage message = new SmsMessage(
                null,
                "+0987654321",
                "Test SMS Body");

        // Act & Assert
        assertDoesNotThrow(() -> provider.send(message));
    }
}
