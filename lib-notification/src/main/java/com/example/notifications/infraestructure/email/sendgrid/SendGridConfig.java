package com.example.notifications.infraestructure.email.sendgrid;

public record SendGridConfig  (String apiKey, String baseUrl) {
    public SendGridConfig {
        if (apiKey == null || baseUrl == null) {
            throw new IllegalArgumentException("apiKey and baseUrl cannot be null");
        }
    }

}
