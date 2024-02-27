package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS

enum class CoffeeChatStatus(
    val category: Category,
    val detail: Detail,
) {
    // MenteeFlow
    MENTEE_APPLY(Category.WAITING, Detail.APPLY),
    MENTOR_REJECT(Category.PASSED, Detail.REJECT),
    MENTOR_APPROVE(Category.SCHEDULED, Detail.APPROVE),
    MENTEE_APPLY_COFFEE_CHAT_COMPLETE(Category.PASSED, Detail.COMPLETE),
    CANCEL_FROM_MENTEE_FLOW(Category.PASSED, Detail.CANCEL),
    AUTO_CANCEL_FROM_MENTEE_FLOW(Category.PASSED, Detail.CANCEL),

    // MentorFlow
    MENTOR_SUGGEST(Category.SUGGESTED, Detail.NONE),
    MENTEE_REJECT(Category.PASSED, Detail.REJECT),
    MENTEE_PENDING(Category.WAITING, Detail.PENDING),
    MENTOR_FINALLY_CANCEL(Category.PASSED, Detail.CANCEL),
    MENTOR_FINALLY_APPROVE(Category.SCHEDULED, Detail.APPROVE),
    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE(Category.PASSED, Detail.COMPLETE),
    CANCEL_FROM_MENTOR_FLOW(Category.PASSED, Detail.CANCEL),
    AUTO_CANCEL_FROM_MENTOR_FLOW(Category.PASSED, Detail.CANCEL),
    ;

    private val cancelableStatus: List<CoffeeChatStatus>
        get() = listOf(
            MENTEE_APPLY,
            MENTOR_APPROVE,
            MENTOR_SUGGEST,
            MENTEE_PENDING,
            MENTOR_FINALLY_APPROVE,
        )

    fun isCancelable(): Boolean = cancelableStatus.any { it == this }

    private val menteeFlow: List<CoffeeChatStatus>
        get() = listOf(
            MENTEE_APPLY,
            MENTOR_REJECT,
            MENTOR_APPROVE,
            MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
            CANCEL_FROM_MENTEE_FLOW,
            AUTO_CANCEL_FROM_MENTEE_FLOW,
        )

    fun isMenteeFlow(): Boolean = menteeFlow.any { it == this }

    companion object {
        @JvmStatic
        fun fromCategory(category: Category): List<CoffeeChatStatus> = entries.filter { it.category == category }

        @JvmStatic
        fun fromCategoryDetail(
            category: Category,
            detail: Detail,
        ): List<CoffeeChatStatus> = entries.filter { it.category == category && it.detail == detail }

        @JvmStatic
        fun withWaitingCategory(): List<CoffeeChatStatus> = entries.filter { it.category == Category.WAITING }

        @JvmStatic
        fun withSuggstedCategory(): List<CoffeeChatStatus> = entries.filter { it.category == Category.SUGGESTED }

        @JvmStatic
        fun withScheduledCategory(): List<CoffeeChatStatus> = entries.filter { it.category == Category.SCHEDULED }

        @JvmStatic
        fun withPassedCategory(): List<CoffeeChatStatus> = entries.filter { it.category == Category.PASSED }
    }

    enum class Category(
        private val value: String,
    ) {
        WAITING("waiting"),
        SUGGESTED("suggested"),
        SCHEDULED("scheduled"),
        PASSED("passed"),
        ;

        companion object {
            @JvmStatic
            fun from(value: String): Category =
                entries.firstOrNull { it.value == value }
                    ?: throw CoffeeChatException(INVALID_COFFEECHAT_STATUS)
        }
    }

    enum class Detail(
        private val value: String,
    ) {
        APPLY("apply"),
        REJECT("reject"),
        APPROVE("approve"),
        PENDING("pending"),
        COMPLETE("complete"),
        CANCEL("cancel"),
        NONE(""),
        ;

        companion object {
            @JvmStatic
            fun from(value: String): Detail =
                entries.firstOrNull { it.value == value }
                    ?: throw CoffeeChatException(INVALID_COFFEECHAT_STATUS)
        }
    }
}
