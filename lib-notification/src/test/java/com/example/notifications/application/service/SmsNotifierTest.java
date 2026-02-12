package com.example.notifications.application.service;

import com.example.notifications.application.port.out.sms.SmsGateway;
import com.example.notifications.application.port.out.sms.SmsGatewayResponse;
import com.example.notifications.application.port.out.sms.SmsMessage;
import com.example.notifications.application.port.out.template.TemplateEngine;
import com.example.notifications.domain.model.SmsNotification;
import com.example.notifications.domain.result.NotificationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SmsNotifierTest {

    @Mock
    private SmsGateway smsGateway;

    @Mock
    private TemplateEngine templateEngine;

    private SmsNotifier notifier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notifier = new SmsNotifier(smsGateway, templateEngine);
    }

    @Test
    void shouldSendSmsSuccessfully() {
        // Arrange
        Map<String, Object> variables = Map.of("name", "Charlie", "code", "9876");
        SmsNotification notification = new SmsNotification(
                "+1234567890",
                "+1234567890",
                "Hello {{name}}, your code is {{code}}",
                variables);

        when(templateEngine.render("Hello {{name}}, your code is {{code}}", variables))
                .thenReturn("Hello Charlie, your code is 9876");
        when(smsGateway.send(any(SmsMessage.class)))
                .thenReturn(new SmsGatewayResponse("sms-123", "queued", null));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());

        verify(templateEngine).render("Hello {{name}}, your code is {{code}}", variables);
        verify(smsGateway).send(any(SmsMessage.class));
    }

    @Test
    void shouldHandleSmsGatewayError() {
        // Arrange
        Map<String, Object> variables = Map.of("user", "Dave");
        SmsNotification notification = new SmsNotification(
                "+1987654321",
                "+1234567890",
                "Message template",
                variables);

        when(templateEngine.render(eq("Message template"), any())).thenReturn("Rendered Message");
        when(smsGateway.send(any(SmsMessage.class)))
                .thenReturn(new SmsGatewayResponse(null, "failed", "Invalid phone number"));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Invalid phone number", result.getErrorMessage());
    }

    @Test
    void shouldRenderTemplatesCorrectly() {
        // Arrange
        Map<String, Object> variables = Map.of("amount", 100, "currency", "USD");
        SmsNotification notification = new SmsNotification(
                "+1122334455","+1234567890",
                "Your payment of {{amount}} {{currency}} was successful",
                variables);

        when(templateEngine.render("Your payment of {{amount}} {{currency}} was successful", variables))
                .thenReturn("Your payment of 100 USD was successful");
        when(smsGateway.send(any(SmsMessage.class)))
                .thenReturn(new SmsGatewayResponse("sms-456", "queued", null));

        // Act
        notifier.send(notification);

        // Assert
        verify(templateEngine).render("Your payment of {{amount}} {{currency}} was successful", variables);
    }

    @Test
    void shouldHandleNullResponse() {
        // Arrange
        Map<String, Object> variables = Map.of();
        SmsNotification notification = new SmsNotification(
                "+1234567890",
                "+1234567890",
                "Test message",
                variables);

        when(templateEngine.render(any(), any())).thenReturn("Rendered");
        when(smsGateway.send(any(SmsMessage.class))).thenReturn(null);

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void shouldHandleResponseWithNullError() {
        // Arrange
        Map<String, Object> variables = Map.of();
        SmsNotification notification = new SmsNotification(
                "+1234567890",
                "+1234567890",
                "Test message",
                variables);

        when(templateEngine.render(any(), any())).thenReturn("Rendered");
        when(smsGateway.send(any(SmsMessage.class)))
                .thenReturn(new SmsGatewayResponse("sms-789", "sent", null));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void validatePhoneModel() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SmsNotification(
                "0234567890",
                "1234567890",
                "Test message",
                Map.of()
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> new SmsNotification(
                "1234567890",
                "",
                "Test message",
                Map.of()
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> new SmsNotification(
                "1234567890",
                "1234567890",
                "",
                Map.of()
        ));
    }
}
