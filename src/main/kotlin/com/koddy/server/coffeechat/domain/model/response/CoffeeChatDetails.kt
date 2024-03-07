package com.koddy.server.coffeechat.domain.model.response

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.global.utils.encrypt.Encryptor
import java.time.LocalDateTime

data class CoffeeChatDetails(
    val id: Long,
    val status: String,
    val applyReason: String?,
    val suggestReason: String?,
    val cancelReason: String?,
    val rejectReason: String?,
    val question: String?,
    val start: LocalDateTime?,
    val end: LocalDateTime?,
    val chatType: String?,
    val chatValue: String?,
    val createdAt: LocalDateTime,
    val lastModifiedAt: LocalDateTime,
) {
    companion object {
        @JvmStatic
        fun of(coffeeChat: CoffeeChat, encryptor: Encryptor): CoffeeChatDetails {
            return CoffeeChatDetails(
                coffeeChat.id,
                coffeeChat.status.name,
                coffeeChat.reason.applyReason,
                coffeeChat.reason.suggestReason,
                coffeeChat.reason.cancelReason,
                coffeeChat.reason.rejectReason,
                coffeeChat.question,
                coffeeChat.reservation?.start,
                coffeeChat.reservation?.end,
                coffeeChat.strategy?.type?.value,
                coffeeChat.strategy?.value?.let { encryptor.decrypt(it) },
                coffeeChat.createdAt,
                coffeeChat.lastModifiedAt,
            )
        }
    }
}
