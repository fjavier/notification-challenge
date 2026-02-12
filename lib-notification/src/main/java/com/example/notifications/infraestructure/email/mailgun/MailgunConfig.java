package com.example.notifications.infraestructure.email.mailgun;

public record MailgunConfig(
        String apiKey,
        String domain,
        String baseUrl
) {

    public MailgunConfig {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Mailgun API key is required");
        }
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Mailgun domain is required");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Mailgun baseUrl is required");
        }
    }
}