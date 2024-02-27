package com.koddy.server.coffeechat.presentation.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.coffeechat.application.usecase.command.CreateMeetingLinkCommand
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider
import com.koddy.server.global.utils.TimeUtils
import jakarta.validation.constraints.NotBlank

data class CreateMeetingLinkRequest(
    @field:NotBlank(message = "Authorization Code는 필수입니다.")
    val authorizationCode: String,

    @field:NotBlank(message = "Redirect Uri는 필수입니다.")
    val redirectUri: String,

    @field:NotBlank(message = "State값은 필수입니다.")
    val state: String,

    @field:NotBlank(message = "회의 제목은 필수입니다")
    val topic: String,

    @field:NotBlank(message = "회의 시작 시간은 필수입니다.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val start: String,

    @field:NotBlank(message = "회의 종료 시간은 필수입니다.")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val end: String,
) {
    fun toCommand(
        memberId: Long,
        provider: String,
    ): CreateMeetingLinkCommand =
        CreateMeetingLinkCommand(
            memberId,
            OAuthProvider.from(provider),
            MeetingLinkProvider.from(provider),
            authorizationCode,
            redirectUri,
            state,
            topic,
            TimeUtils.toLocalDateTime(start),
            TimeUtils.toLocalDateTime(end),
        )
}
