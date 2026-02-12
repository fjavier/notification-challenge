package com.example.notifications.infraestructure.email.sendgrid;

public record SendGridResponse(String messageId, String message, String status,  String errors) {
}
