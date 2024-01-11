package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.coffeechat.presentation.dto.request.CreateMeetingLinkRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static com.koddy.server.acceptance.CommonRequestFixture.deleteRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequest;
import static com.koddy.server.common.fixture.OAuthFixture.GOOGLE_MENTOR_1;
import static com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI;
import static com.koddy.server.common.utils.OAuthUtils.STATE;

public class CoffeeChatAcceptanceStep {
    public static ValidatableResponse 커피챗_링크를_자동_생성한다(final String provider, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/{provider}/meetings")
                .build(provider)
                .getPath();

        final CreateMeetingLinkRequest request = new CreateMeetingLinkRequest(
                GOOGLE_MENTOR_1.getAuthorizationCode(),
                REDIRECT_URI,
                STATE,
                "줌 회의 Hello",
                LocalDateTime.of(2024, 1, 10, 18, 0),
                LocalDateTime.of(2024, 1, 10, 19, 0)
        );

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 자동_생성한_커피챗_링크를_삭제한다(
            final String provider,
            final String meetingId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/oauth/{provider}/meetings/{meetingId}")
                .build(provider, meetingId)
                .getPath();

        return deleteRequest(accessToken, uri);
    }
}
