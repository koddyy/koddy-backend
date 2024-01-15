package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.coffeechat.presentation.dto.request.CreateMeetingLinkRequest;
import com.koddy.server.coffeechat.presentation.dto.request.MenteeApplyCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.MentorSuggestCoffeeChatRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static com.koddy.server.acceptance.CommonRequestFixture.deleteRequestWithAccessToken;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequestWithAccessToken;
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

        return postRequestWithAccessToken(uri, request, accessToken);
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

        return deleteRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 멘토가_멘티에게_커피챗을_제안한다(
            final long menteeId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/suggest/{menteeId}")
                .build(menteeId)
                .getPath();

        final MentorSuggestCoffeeChatRequest request = new MentorSuggestCoffeeChatRequest("신청 이유...");

        return postRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘티가_멘토에게_커피챗을_신청한다(
            final LocalDateTime start,
            final LocalDateTime end,
            final long mentorId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/apply/{mentorId}")
                .build(mentorId)
                .getPath();

        final MenteeApplyCoffeeChatRequest request = new MenteeApplyCoffeeChatRequest("신청 이유...", start, end);

        return postRequestWithAccessToken(uri, request, accessToken);
    }
}
