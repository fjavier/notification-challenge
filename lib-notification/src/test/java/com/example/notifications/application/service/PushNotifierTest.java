package com.example.notifications.application.service;

import com.example.notifications.application.port.out.push.PushGateway;
import com.example.notifications.application.port.out.push.PushGatewayResponse;
import com.example.notifications.application.port.out.push.PushMessage;
import com.example.notifications.application.port.out.template.TemplateEngine;
import com.example.notifications.domain.model.PushNotification;
import com.example.notifications.domain.result.NotificationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PushNotifierTest {

    @Mock
    private PushGateway pushGateway;

    @Mock
    private TemplateEngine templateEngine;

    private PushNotifier notifier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notifier = new PushNotifier(pushGateway, templateEngine);
    }

    @Test
    void shouldSendPushNotificationSuccessfully() {
        // Arrange
        Map<String, Object> variables = Map.of("user", "Bob");
        Map<String, String> data = Map.of("action", "open_app");
        PushNotification notification = new PushNotification(
                "bob@example.com",
                "device-token-123",
                "Hello {{user}}",
                "You have a new message, {{user}}!",
                variables,
                data);

        when(templateEngine.render("Hello {{user}}", variables)).thenReturn("Hello Bob");
        when(templateEngine.render("You have a new message, {{user}}!", variables))
                .thenReturn("You have a new message, Bob!");
        when(pushGateway.send(any(PushMessage.class)))
                .thenReturn(new PushGatewayResponse("push-msg-123", "success", null));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());

        verify(templateEngine).render("Hello {{user}}", variables);
        verify(templateEngine).render("You have a new message, {{user}}!", variables);
        verify(pushGateway).send(any(PushMessage.class));
    }

    @Test
    void shouldHandlePushGatewayError() {
        // Arrange
        Map<String, Object> variables = Map.of("item", "Product");
        Map<String, String> data = Map.of();
        PushNotification notification = new PushNotification(
                "user@example.com",
                "device-token-456",
                "Title",
                "Body",
                variables,
                data);

        when(templateEngine.render(eq("Title"), any())).thenReturn("Rendered Title");
        when(templateEngine.render(eq("Body"), any())).thenReturn("Rendered Body");
        when(pushGateway.send(any(PushMessage.class)))
                .thenReturn(new PushGatewayResponse(null, "failed", "Invalid device token"));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Invalid device token", result.getErrorMessage());
    }

    @Test
    void shouldRenderTemplatesCorrectly() {
        // Arrange
        Map<String, Object> variables = Map.of("count", 5, "type", "messages");
        Map<String, String> data = Map.of("screen", "inbox");
        PushNotification notification = new PushNotification(
                "user@example.com",
                "device-token-789",
                "{{count}} new {{type}}",
                "You have {{count}} new {{type}} waiting",
                variables,
                data);

        when(templateEngine.render("{{count}} new {{type}}", variables)).thenReturn("5 new messages");
        when(templateEngine.render("You have {{count}} new {{type}} waiting", variables))
                .thenReturn("You have 5 new messages waiting");
        when(pushGateway.send(any(PushMessage.class)))
                .thenReturn(new PushGatewayResponse("push-msg-789", "success", null));

        // Act
        notifier.send(notification);

        // Assert
        verify(templateEngine).render("{{count}} new {{type}}", variables);
        verify(templateEngine).render("You have {{count}} new {{type}} waiting", variables);
    }

    @Test
    void shouldHandleEmptyErrorsAsSuccess() {
        // Arrange
        Map<String, Object> variables = Map.of();
        Map<String, String> data = Map.of();
        PushNotification notification = new PushNotification(
                "user@example.com",
                "device-token-000",
                "Title",
                "Body",
                variables,
                data);

        when(templateEngine.render(any(), any())).thenReturn("Rendered");
        when(pushGateway.send(any(PushMessage.class)))
                .thenReturn(new PushGatewayResponse("push-msg-000", "success", ""));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertTrue(result.isSuccess());
    }
}
