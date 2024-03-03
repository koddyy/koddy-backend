package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_STRATEGY
import com.koddy.server.global.utils.encrypt.Encryptor
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Lob

@Embeddable
data class Strategy(
    @Enumerated(STRING)
    @Column(name = "chat_type", columnDefinition = "VARCHAR(30)")
    val type: Type,

    @Lob
    @Column(name = "chat_type_value", columnDefinition = "TEXT")
    val value: String,
) {
    companion object {
        @JvmStatic
        fun of(
            type: Type,
            value: String,
            encryptor: Encryptor,
        ): Strategy {
            return Strategy(
                type = type,
                value = encryptor.encrypt(value),
            )
        }
    }

    enum class Type(
        val value: String,
    ) {
        ZOOM_LINK("zoom"),
        GOOGLE_MEET_LINK("google"),
        KAKAO_ID("kakao"),
        LINK_ID("line"),
        WECHAT_ID("wechat"),
        ;

        companion object {
            fun from(value: String): Type {
                return entries.firstOrNull { it.value == value }
                    ?: throw CoffeeChatException(INVALID_MEETING_STRATEGY)
            }
        }
    }
}
