package com.koddy.server.common.mock.stub

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse
import com.koddy.server.common.fixture.OAuthFixture
import com.koddy.server.global.log.logger
import org.slf4j.Logger

open class StubMeetingLinkManager : MeetingLinkManager {
    private val log: Logger = logger()

    override fun fetchToken(
        provider: OAuthProvider,
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthTokenResponse = OAuthFixture.parseOAuthTokenByCode(code)

    override fun create(
        provider: MeetingLinkProvider,
        accessToken: String,
        meetingLinkRequest: MeetingLinkRequest,
    ): MeetingLinkResponse {
        log.info("Meeting 생성")
        return ZoomMeetingLinkResponse(
            "zoom-meeting-id",
            "sjiwon4491@gmail.com",
            "Hello 줌 회의",
            "zoom-join-url",
            60,
        )
    }

    override fun delete(
        provider: MeetingLinkProvider,
        meetingId: String,
    ) = log.info("Meeting [{}] 삭제", meetingId)
}
