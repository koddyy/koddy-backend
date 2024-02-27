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
        @JvmStatic
        fun from(provider: String): MeetingLinkProvider =
            entries.firstOrNull { it.provider == provider }
                ?: throw CoffeeChatException(INVALID_MEETING_LINK_PROVIDER)
    }
}
