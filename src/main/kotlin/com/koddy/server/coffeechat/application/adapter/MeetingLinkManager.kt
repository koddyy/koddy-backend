package com.koddy.server.coffeechat.application.adapter

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse

interface MeetingLinkManager {
    fun fetchToken(
        provider: OAuthProvider,
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthTokenResponse

    fun create(
        provider: MeetingLinkProvider,
        oAuthAccessToken: String,
        meetingLinkRequest: MeetingLinkRequest,
    ): MeetingLinkResponse

    fun delete(
        provider: MeetingLinkProvider,
        meetingId: String,
    )
}
