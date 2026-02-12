package com.example.notifications.infraestructure.email.mailgun;

public record MailgunRequest(
        String from,
        String to,
        String subject,
        String text,
        String domain
) {}