package com.example.notifications.infraestructure.push.firebase;

public record FirebaseConfig(
        String projectId,
        String serviceAccountKey
) {

    public FirebaseConfig {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("ProjectId is required");
        }
        if (serviceAccountKey == null || serviceAccountKey.isBlank()) {
            throw new IllegalArgumentException("Service account key is required");
        }
    }
}
