package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.coffeechat.presentation.dto.request.ApproveAppliedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.ApprovePendingCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.CreateMeetingLinkRequest;
import com.koddy.server.coffeechat.presentation.dto.request.MenteeApplyCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.MentorSuggestCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.PendingSuggestedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.RejectAppliedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.RejectPendingCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.RejectSuggestedCoffeeChatRequest;
import com.koddy.server.common.fixture.StrategyFixture;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static com.koddy.server.acceptance.CommonRequestFixture.deleteRequestWithAccessToken;
import static com.koddy.server.acceptance.CommonRequestFixture.patchRequestWithAccessToken;
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
                LocalDateTime.of(2024, 1, 10, 18, 0).toString(),
                LocalDateTime.of(2024, 1, 10, 19, 0).toString()
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
                .fromPath("/api/coffeechats/suggest")
                .build()
                .toUri()
                .getPath();

        final MentorSuggestCoffeeChatRequest request = new MentorSuggestCoffeeChatRequest(menteeId, "제안 이유...");

        return postRequestWithAccessToken(uri, request, accessToken);
    }

    public static long 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
            final long menteeId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/suggest")
                .build()
                .toUri()
                .getPath();

        final MentorSuggestCoffeeChatRequest request = new MentorSuggestCoffeeChatRequest(menteeId, "제안 이유...");

        return postRequestWithAccessToken(uri, request, accessToken)
                .extract()
                .jsonPath()
                .getLong("coffeeChatId");
    }

    public static ValidatableResponse 멘티가_멘토에게_커피챗을_신청한다(
            final LocalDateTime start,
            final LocalDateTime end,
            final long mentorId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/apply")
                .build()
                .toUri()
                .getPath();

        final MenteeApplyCoffeeChatRequest request = new MenteeApplyCoffeeChatRequest(
                mentorId,
                "신청 이유...",
                start.toString(),
                end.toString()
        );

        return postRequestWithAccessToken(uri, request, accessToken);
    }

    public static long 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
            final LocalDateTime start,
            final LocalDateTime end,
            final long mentorId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/apply")
                .build()
                .toUri()
                .getPath();

        final MenteeApplyCoffeeChatRequest request = new MenteeApplyCoffeeChatRequest(
                mentorId,
                "신청 이유...",
                start.toString(),
                end.toString()
        );

        return postRequestWithAccessToken(uri, request, accessToken)
                .extract()
                .jsonPath()
                .getLong("coffeeChatId");
    }

    public static ValidatableResponse 멘토가_멘티의_커피챗_신청을_거절한다(
            final long coffeeChatId,
            final String rejectReason,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/applied/reject/{coffeeChatId}")
                .build(coffeeChatId)
                .getPath();

        final RejectAppliedCoffeeChatRequest request = new RejectAppliedCoffeeChatRequest(rejectReason);

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토가_멘티의_커피챗_신청을_수락한다(
            final long coffeeChatId,
            final StrategyFixture fixture,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/applied/approve/{coffeeChatId}")
                .build(coffeeChatId)
                .getPath();

        final ApproveAppliedCoffeeChatRequest request = new ApproveAppliedCoffeeChatRequest(fixture.getType().getEng(), fixture.getValue());

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘티가_멘토의_커피챗_제안을_거절한다(
            final long coffeeChatId,
            final String rejectReason,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/suggested/reject/{coffeeChatId}")
                .build(coffeeChatId)
                .getPath();

        final RejectSuggestedCoffeeChatRequest request = new RejectSuggestedCoffeeChatRequest(rejectReason);

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘티가_멘토의_커피챗_제안을_1차_수락한다(
            final long coffeeChatId,
            final LocalDateTime start,
            final LocalDateTime end,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/suggested/pending/{coffeeChatId}")
                .build(coffeeChatId)
                .getPath();

        final PendingSuggestedCoffeeChatRequest request = new PendingSuggestedCoffeeChatRequest("질문..", start.toString(), end.toString());

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다(
            final long coffeeChatId,
            final String rejectReason,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/pending/reject/{coffeeChatId}")
                .build(coffeeChatId)
                .getPath();

        final RejectPendingCoffeeChatRequest request = new RejectPendingCoffeeChatRequest(rejectReason);

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(
            final long coffeeChatId,
            final StrategyFixture fixture,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/pending/approve/{coffeeChatId}")
                .build(coffeeChatId)
                .getPath();

        final ApprovePendingCoffeeChatRequest request = new ApprovePendingCoffeeChatRequest(fixture.getType().getEng(), fixture.getValue());

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 신청_제안한_커피챗을_취소한다(
            final long coffeeChatId,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/coffeechats/cancel/{coffeeChatId}")
                .build(coffeeChatId)
                .getPath();

        return deleteRequestWithAccessToken(uri, accessToken);
    }
}
