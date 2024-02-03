package com.koddy.server.notification.domain.model;

public record NotificationMessage(
        String from,
        String rejectReason
) {
    public static NotificationMessage from(final String from) {
        return new NotificationMessage(from, null);
    }

    public static NotificationMessage of(final String from, final String rejectReason) {
        return new NotificationMessage(from, rejectReason);
    }
}
