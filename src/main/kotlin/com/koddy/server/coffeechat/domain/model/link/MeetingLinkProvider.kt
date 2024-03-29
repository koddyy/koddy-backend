package com.koddy.server.coffeechat.domain.model.link

import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER

enum class MeetingLinkProvider(
    val provider: String,
) {
    ZOOM("zoom"),
    GOOGLE("google"),
    ;

    companion object {
        fun from(provider: String): MeetingLinkProvider {
            return entries.firstOrNull { it.provider == provider }
                ?: throw CoffeeChatException(INVALID_MEETING_LINK_PROVIDER)
        }
    }
}
