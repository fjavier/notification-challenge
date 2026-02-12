package com.example.notifications.infraestructure.email.sendgrid;

public record SendGridRequest(String from, String to, String subject, String body) {
}
