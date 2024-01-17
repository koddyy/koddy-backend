package com.koddy.server.notification.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koddy.server.notification.domain.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessageConverter {
    private final ObjectMapper objectMapper;

    public String toFlatMessage(final NotificationMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public NotificationMessage toNotificationMessage(final String message) {
        try {
            return objectMapper.readValue(message, NotificationMessage.class);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
