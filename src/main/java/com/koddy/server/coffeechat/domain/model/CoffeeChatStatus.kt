package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode
import java.util.stream.Stream

enum class CoffeeChatStatus(
    val category: String,
    val detail: String,
) {
    // MenteeFlow
    MENTEE_APPLY("waiting", "apply"),
    MENTEE_CANCEL("passed", "cancel"),
    MENTOR_REJECT("passed", "reject"),
    MENTOR_APPROVE("scheduled", "approve"),
    MENTEE_APPLY_COFFEE_CHAT_COMPLETE("passed", "complete"),

    // MentorFlow
    MENTOR_SUGGEST("suggested", ""),
    MENTOR_CANCEL("passed", "cancel"),
    MENTEE_REJECT("passed", "reject"),
    MENTEE_PENDING("waiting", "pending"),
    MENTOR_FINALLY_REJECT("passed", "reject"),
    MENTOR_FINALLY_APPROVE("scheduled", "approve"),
    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE("passed", "complete"),
    ;

    val isMenteeCannotCancel: Boolean
        get() = Stream.of(MENTEE_APPLY, MENTOR_APPROVE).noneMatch { it == this }

    val isMentorCannotCancel: Boolean
        get() = Stream.of(MENTOR_SUGGEST, MENTEE_PENDING, MENTOR_FINALLY_APPROVE).noneMatch { it == this }

    companion object {
        @JvmStatic
        fun fromCategory(category: String): List<CoffeeChatStatus> {
            if (isAnonymousCategory(category)) {
                throw CoffeeChatException(CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS)
            }

            return entries.filter { it.category == category }
        }

        private fun isAnonymousCategory(category: String): Boolean = entries.none { it.category == category }

        @JvmStatic
        fun fromCategoryDetail(
            category: String,
            detail: String,
        ): List<CoffeeChatStatus> {
            if (isAnonymousCategoryDetail(category, detail)) {
                throw CoffeeChatException(CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS)
            }

            return entries.filter { it.category == category && it.detail == detail }
        }

        private fun isAnonymousCategoryDetail(
            category: String,
            detail: String,
        ): Boolean = entries.none { it.category == category && it.detail == detail }

        @JvmStatic
        fun withWaitingCategory(): List<CoffeeChatStatus> =
            listOf(
                MENTEE_APPLY,
                MENTEE_PENDING,
            )

        @JvmStatic
        fun withSuggstedCategory(): List<CoffeeChatStatus> = listOf(MENTOR_SUGGEST)

        @JvmStatic
        fun withScheduledCategory(): List<CoffeeChatStatus> =
            listOf(
                MENTOR_APPROVE,
                MENTOR_FINALLY_APPROVE,
            )

        @JvmStatic
        fun withPassedCategory(): List<CoffeeChatStatus> =
            listOf(
                MENTEE_CANCEL,
                MENTOR_REJECT,
                MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                MENTOR_CANCEL,
                MENTEE_REJECT,
                MENTOR_FINALLY_REJECT,
                MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE,
            )
    }
}
