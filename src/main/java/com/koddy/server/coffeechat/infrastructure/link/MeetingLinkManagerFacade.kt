package com.koddy.server.coffeechat.infrastructure.link

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthConnector
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthConnector
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse
import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER
import com.koddy.server.coffeechat.infrastructure.link.zoom.ZoomMeetingLinkProcessor
import org.springframework.stereotype.Component

@Component
class MeetingLinkManagerFacade(
    private val zoomOAuthConnector: ZoomOAuthConnector,
    private val zoomMeetingLinkProcessor: ZoomMeetingLinkProcessor,
    private val googleOAuthConnector: GoogleOAuthConnector,
) : MeetingLinkManager {
    override fun fetchToken(
        provider: OAuthProvider,
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthTokenResponse =
        when (provider) {
            OAuthProvider.ZOOM -> zoomOAuthConnector.fetchToken(code, redirectUri, state)
            OAuthProvider.GOOGLE -> googleOAuthConnector.fetchToken(code, redirectUri, state)
            OAuthProvider.KAKAO -> throw CoffeeChatException(INVALID_MEETING_LINK_PROVIDER)
        }

    override fun create(
        provider: MeetingLinkProvider,
        oAuthAccessToken: String,
        meetingLinkRequest: MeetingLinkRequest,
    ): MeetingLinkResponse =
        when (provider) {
            MeetingLinkProvider.ZOOM -> zoomMeetingLinkProcessor.create(oAuthAccessToken, meetingLinkRequest)
            MeetingLinkProvider.GOOGLE -> throw UnsupportedOperationException("not supported yet...")
        }

    override fun delete(provider: MeetingLinkProvider, meetingId: String) =
        when (provider) {
            MeetingLinkProvider.ZOOM -> zoomMeetingLinkProcessor.delete(meetingId)
            MeetingLinkProvider.GOOGLE -> throw UnsupportedOperationException("not supported yet...")
        }
}
