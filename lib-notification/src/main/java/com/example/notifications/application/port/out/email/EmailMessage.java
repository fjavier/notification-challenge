package com.example.notifications.application.port.out.email;

public record EmailMessage(String from, String to, String subject, String body) {
}
