package com.example.notifications.infraestructure.push.firebase;

import com.example.notifications.application.port.out.push.PushGatewayResponse;
import com.example.notifications.application.port.out.push.PushMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FirebasePushProviderTest {

    private FirebasePushProvider provider;

    @BeforeEach
    void setUp() {
        FirebaseConfig config = new FirebaseConfig("test-project-id", "test-service-account-key");
        provider = new FirebasePushProvider(config);
    }

    @Test
    void shouldSendPushNotificationSuccessfully() {
        // Arrange
        PushMessage message = new PushMessage(
                "test-token",
                "Test Title",
                "Test Body",
                Map.of("key", "value")
        );

        // Act
        PushGatewayResponse response = provider.send(message);

        // Assert
        assertNotNull(response);
        assertNotNull(response.messageId());
        assertTrue(response.messageId().startsWith("firebase-"));
        assertEquals("success", response.status());
        assertNull(response.errors());
    }

    @Test
    void shouldGenerateDifferentMessageIds() throws InterruptedException {
        // Arrange
        PushMessage message = new PushMessage(
                "test-token",
                "Test Title",
                "Test Body",
                Map.of("key", "value")
        );

        // Act
        PushGatewayResponse response1 = provider.send(message);
        Thread.sleep(2); // Ensure timestamps are different
        PushGatewayResponse response2 = provider.send(message);

        // Assert
        assertNotNull(response1.messageId());
        assertNotNull(response2.messageId());
        assertNotEquals(response1.messageId(), response2.messageId());
    }

    @Test
    void shouldNotThrowExceptionWhenSending() {
        // Arrange
        PushMessage message = new PushMessage(
                "test-token",
                "Test Title",
                "Test Body",
                Map.of("key", "value")
        );

        // Act & Assert
        assertDoesNotThrow(() -> provider.send(message));
    }
}
