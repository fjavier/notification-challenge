package com.example.notifications.infraestructure.push.firebase;

import java.util.Map;

public record FirebaseRequest(
        String token,
        String title,
        String body,
        Map<String, String> data
) {}
