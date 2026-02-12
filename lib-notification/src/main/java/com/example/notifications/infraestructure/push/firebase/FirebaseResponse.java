package com.example.notifications.infraestructure.push.firebase;

public record FirebaseResponse(
        String messageId,
        String status,
        String errors
) {}
