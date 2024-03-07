package com.koddy.server.coffeechat.application.usecase.command

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider
import java.time.LocalDateTime

data class CreateMeetingLinkCommand(
    val memberId: Long,
    val oAuthProvider: OAuthProvider,
    val linkProvider: MeetingLinkProvider,
    val code: String,
    val redirectUri: String,
    val state: String,
    val topic: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
)

data class DeleteMeetingLinkCommand(
    val linkProvider: MeetingLinkProvider,
    val meetingId: String,
)
