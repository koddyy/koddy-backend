package com.koddy.server.coffeechat.infrastructure.link

import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse

interface MeetingLinkProcessor {
    fun create(
        oAuthAccessToken: String,
        meetingLinkRequest: MeetingLinkRequest,
    ): MeetingLinkResponse

    fun delete(meetingId: String)

    companion object {
        const val OAUTH_CONTENT_TYPE: String = "application/json"
        const val BEARER_TOKEN_TYPE: String = "Bearer"
    }
}
