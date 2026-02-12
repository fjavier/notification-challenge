package com.example.notifications.infraestructure.chat.slack;

public record SlackConfig(
        // === Autenticación ===
        String webhookUrl,     // para Incoming Webhooks
        String botToken,       // para Slack Bot API (opcional)

        // === Sender identity (opcional) ===
        String defaultChannel, // "#alerts"
        String username,       // "Deploy Bot"
        String iconEmoji,      // ":rocket:"
        String iconUrl,        // alternativa al emoji

        // === Config técnica ===
        String baseUrl        // default https://slack.com/api
) {
}
