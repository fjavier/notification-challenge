package com.example.notifications.infraestructure.email.mailgun;

import java.util.List;

public record MailgunResponse(
        String id,
        String message,
        String errors
) {}