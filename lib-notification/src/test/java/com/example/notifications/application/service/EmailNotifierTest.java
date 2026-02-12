package com.example.notifications.application.service;

import com.example.notifications.application.port.out.email.EmailGateway;
import com.example.notifications.application.port.out.email.EmailGatewayResponse;
import com.example.notifications.application.port.out.email.EmailMessage;
import com.example.notifications.application.port.out.template.TemplateEngine;
import com.example.notifications.domain.model.EmailNotification;
import com.example.notifications.domain.result.NotificationResult;
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

class EmailNotifierTest {

    @Mock
    private EmailGateway emailGateway;

    @Mock
    private TemplateEngine templateEngine;

    private EmailNotifier notifier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notifier = new EmailNotifier(emailGateway, templateEngine);
    }

    @Test
    void shouldSendEmailSuccessfully() {
        // Arrange
        Map<String, Object> variables = Map.of("name", "John");
        EmailNotification notification = new EmailNotification(
                "recipient@example.com",
                "Hello {{name}}",
                "Welcome {{name}}!",
                variables);

        when(templateEngine.render("Hello {{name}}", variables)).thenReturn("Hello John");
        when(templateEngine.render("Welcome {{name}}!", variables)).thenReturn("Welcome John!");
        when(emailGateway.send(any(EmailMessage.class)))
                .thenReturn(new EmailGatewayResponse("msg-123", "accepted", null));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());

        verify(templateEngine).render("Hello {{name}}", variables);
        verify(templateEngine).render("Welcome {{name}}!", variables);
        verify(emailGateway).send(any(EmailMessage.class));
    }

    @Test
    void shouldHandleEmailGatewayError() {
        // Arrange
        Map<String, Object> variables = Map.of("name", "Jane");
        EmailNotification notification = new EmailNotification(
                "recipient@example.com",
                "Subject",
                "Body",
                variables);

        when(templateEngine.render(eq("Subject"), any())).thenReturn("Rendered Subject");
        when(templateEngine.render(eq("Body"), any())).thenReturn("Rendered Body");
        when(emailGateway.send(any(EmailMessage.class)))
                .thenReturn(new EmailGatewayResponse(null, "failed", "SMTP error"));

        // Act
        NotificationResult result = notifier.send(notification);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("SMTP error", result.getErrorMessage());
    }

    @Test
    void shouldRenderTemplatesCorrectly() {
        // Arrange
        Map<String, Object> variables = Map.of("user", "Alice", "code", "12345");
        EmailNotification notification = new EmailNotification(
                "alice@example.com",
                "Your code: {{code}}",
                "Hello {{user}}, your verification code is {{code}}",
                variables);

        when(templateEngine.render("Your code: {{code}}", variables)).thenReturn("Your code: 12345");
        when(templateEngine.render("Hello {{user}}, your verification code is {{code}}", variables))
                .thenReturn("Hello Alice, your verification code is 12345");
        when(emailGateway.send(any(EmailMessage.class)))
                .thenReturn(new EmailGatewayResponse("msg-456", "accepted", null));

        // Act
        notifier.send(notification);

        // Assert
        verify(templateEngine).render("Your code: {{code}}", variables);
        verify(templateEngine).render("Hello {{user}}, your verification code is {{code}}", variables);
    }
}
