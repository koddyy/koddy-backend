package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager
import com.koddy.server.coffeechat.application.adapter.MeetingLinkTokenCashier
import com.koddy.server.coffeechat.application.usecase.command.CreateMeetingLinkCommand
import com.koddy.server.coffeechat.application.usecase.command.DeleteMeetingLinkCommand
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest
import com.koddy.server.global.annotation.UseCase
import java.time.Duration

@UseCase
class ManageMeetingLinkUseCase(
    private val meetingLinkManager: MeetingLinkManager,
    private val meetingLinkTokenCashier: MeetingLinkTokenCashier,
) {
    /**
     * 추후 Google Meet Creator 연동하면서 MeetingLinkCreator Request/Response 스펙 재정의 (Interface)
     */
    fun create(command: CreateMeetingLinkCommand): MeetingLinkResponse =
        meetingLinkManager.create(
            provider = command.linkProvider,
            oAuthAccessToken = getOAuthAccessToken(command),
            meetingLinkRequest = createRequest(command),
        )

    private fun getOAuthAccessToken(command: CreateMeetingLinkCommand): String {
        if (meetingLinkTokenCashier.containsViaPlatformId(command.memberId)) {
            return meetingLinkTokenCashier.getViaPlatformId(command.memberId)
        }

        val token: OAuthTokenResponse = meetingLinkManager.fetchToken(
            provider = command.oAuthProvider,
            code = command.code,
            redirectUri = command.redirectUri,
            state = command.state,
        )
        meetingLinkTokenCashier.storeViaPlatformId(
            platformId = command.memberId,
            oAuthAccessToken = token.accessToken(),
            duration = Duration.ofMinutes(10),
        )
        return token.accessToken()
    }

    private fun createRequest(command: CreateMeetingLinkCommand): ZoomMeetingLinkRequest =
        ZoomMeetingLinkRequest(
            topic = command.topic,
            start = command.start,
            end = command.end,
        )

    fun delete(command: DeleteMeetingLinkCommand) =
        meetingLinkManager.delete(
            provider = command.linkProvider,
            meetingId = command.meetingId,
        )
}
