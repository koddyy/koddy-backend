package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.RequestHelper
import com.koddy.server.coffeechat.presentation.request.ApproveAppliedCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.CancelCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatByApplyRequest
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatBySuggestRequest
import com.koddy.server.coffeechat.presentation.request.CreateMeetingLinkRequest
import com.koddy.server.coffeechat.presentation.request.FinallyApprovePendingCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.FinallyCancelPendingCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.PendingSuggestedCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.RejectAppliedCoffeeChatRequest
import com.koddy.server.coffeechat.presentation.request.RejectSuggestedCoffeeChatRequest
import com.koddy.server.common.fixture.MentorFixtureStore
import com.koddy.server.common.fixture.StrategyFixture
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI
import com.koddy.server.common.utils.OAuthUtils.STATE
import com.koddy.server.common.utils.OAuthUtils.mentorAuthorizationCode
import io.restassured.response.ValidatableResponse
import java.time.LocalDateTime

object CoffeeChatAcceptanceStep {
    fun 커피챗_링크를_자동_생성한다(
        provider: String,
        fixture: MentorFixtureStore.MentorFixture,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.postRequestWithAccessToken(
            uri = "/api/oauth/$provider/meetings",
            body = CreateMeetingLinkRequest(
                authorizationCode = mentorAuthorizationCode(fixture.id),
                redirectUri = REDIRECT_URI,
                state = STATE,
                topic = "줌 회의 Hello",
                start = "2024/1/10-18:00".toLocalDateTime().toString(),
                end = "2024/1/10-18:30".toLocalDateTime().toString(),
            ),
            accessToken = accessToken,
        )
    }

    fun 자동_생성한_커피챗_링크를_삭제한다(
        provider: String,
        meetingId: String,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.deleteRequestWithAccessToken(
            uri = "/api/oauth/$provider/meetings/$meetingId",
            accessToken = accessToken,
        )
    }

    fun 멘토가_멘티에게_커피챗을_제안한다(
        menteeId: Long,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.postRequestWithAccessToken(
            uri = "/api/coffeechats/suggest",
            body = CreateCoffeeChatBySuggestRequest(
                menteeId = menteeId,
                suggestReason = "제안 이유...",
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
        menteeId: Long,
        accessToken: String,
    ): Long {
        return 멘토가_멘티에게_커피챗을_제안한다(
            menteeId = menteeId,
            accessToken = accessToken,
        ).extract()
            .jsonPath()
            .getLong("result")
    }

    fun 멘티가_멘토에게_커피챗을_신청한다(
        start: LocalDateTime,
        end: LocalDateTime,
        mentorId: Long,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.postRequestWithAccessToken(
            uri = "/api/coffeechats/apply",
            body = CreateCoffeeChatByApplyRequest(
                mentorId = mentorId,
                applyReason = "신청 이유...",
                start = start.toString(),
                end = end.toString(),
            ),
            accessToken = accessToken,
        )
    }

    fun 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
        start: LocalDateTime,
        end: LocalDateTime,
        mentorId: Long,
        accessToken: String,
    ): Long {
        return 멘티가_멘토에게_커피챗을_신청한다(
            start = start,
            end = end,
            mentorId = mentorId,
            accessToken = accessToken,
        ).extract()
            .jsonPath()
            .getLong("result")
    }

    fun 신청_제안한_커피챗을_취소한다(
        coffeeChatId: Long,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/coffeechats/cancel/$coffeeChatId",
            body = CancelCoffeeChatRequest(cancelReason = "취소"),
            accessToken = accessToken,
        )
    }

    fun 멘토가_멘티의_커피챗_신청을_거절한다(
        coffeeChatId: Long,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/coffeechats/applied/reject/$coffeeChatId",
            body = RejectAppliedCoffeeChatRequest(rejectReason = "거절.."),
            accessToken = accessToken,
        )
    }

    fun 멘토가_멘티의_커피챗_신청을_수락한다(
        coffeeChatId: Long,
        fixture: StrategyFixture,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/coffeechats/applied/approve/$coffeeChatId",
            body = ApproveAppliedCoffeeChatRequest(
                question = "질문..",
                chatType = fixture.type.value,
                chatValue = fixture.value,
            ),
            accessToken = accessToken,
        )
    }

    fun 멘티가_멘토의_커피챗_제안을_거절한다(
        coffeeChatId: Long,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/coffeechats/suggested/reject/$coffeeChatId",
            body = RejectSuggestedCoffeeChatRequest(rejectReason = "거절.."),
            accessToken = accessToken,
        )
    }

    fun 멘티가_멘토의_커피챗_제안을_1차_수락한다(
        coffeeChatId: Long,
        start: LocalDateTime,
        end: LocalDateTime,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/coffeechats/suggested/pending/$coffeeChatId",
            body = PendingSuggestedCoffeeChatRequest(
                question = "질문..",
                start = start.toString(),
                end = end.toString(),
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(
        coffeeChatId: Long,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/coffeechats/pending/cancel/$coffeeChatId",
            body = FinallyCancelPendingCoffeeChatRequest(cancelReason = "최종 취소.."),
            accessToken = accessToken,
        )
    }

    fun 멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(
        coffeeChatId: Long,
        fixture: StrategyFixture,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/coffeechats/pending/approve/$coffeeChatId",
            body = FinallyApprovePendingCoffeeChatRequest(
                chatType = fixture.type.value,
                chatValue = fixture.value,
            ),
            accessToken = accessToken,
        )
    }

    fun 내_일정_커피챗_상세_조회를_진행한다(
        coffeeChatId: Long,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/coffeechats/$coffeeChatId",
            accessToken = accessToken,
        )
    }
}
