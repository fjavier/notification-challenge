package com.example.notifications.domain.result;

public class NotificationResult {

    private final boolean success;
    private final String errorMessage;

    private NotificationResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static NotificationResult success() {
        return new NotificationResult(true, null);
    }

    public static NotificationResult failure(String error) {
        return new NotificationResult(false, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
