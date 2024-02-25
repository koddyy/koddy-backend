package com.koddy.server.acceptance.notification;

import com.koddy.server.notification.application.usecase.query.response.NotificationSummary;
import com.koddy.server.notification.domain.model.NotificationType;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.koddy.server.acceptance.CommonRequestFixture.getRequestWithAccessToken;
import static com.koddy.server.acceptance.CommonRequestFixture.patchRequestWithAccessToken;

public class NotificationAcceptanceStep {
    public static ValidatableResponse 알림을_조회한다(
            final int page,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/notifications/me?page={page}")
                .build(page)
                .getPath();

        return getRequestWithAccessToken(uri, accessToken);
    }

    public static long 특정_타입의_알림_ID를_조회한다(
            final NotificationType type,
            final int page,
            final String accessToken
    ) {
        final List<NotificationSummary> result = 알림을_조회한다(page, accessToken)
                .extract()
                .jsonPath()
                .getList("result", NotificationSummary.class);

        return result.stream()
                .filter(it -> it.getType().equals(type.name()))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getId();
    }

    public static ValidatableResponse 단건_알림을_읽음_처리한다(
            final long notificationId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/notifications/{notificationId}/read")
                .build(notificationId)
                .getPath();

        return patchRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 전체_알림을_읽음_처리한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/notifications/me/read-all")
                .build()
                .toUri()
                .getPath();

        return patchRequestWithAccessToken(uri, accessToken);
    }
}
