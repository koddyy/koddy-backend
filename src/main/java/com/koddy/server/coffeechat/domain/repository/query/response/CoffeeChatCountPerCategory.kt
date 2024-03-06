package com.koddy.server.coffeechat.domain.repository.query.response

data class CoffeeChatCountPerCategory(
    val waiting: Long = 0L,
    val suggested: Long = 0L,
    val scheduled: Long = 0L,
    val passed: Long = 0L,
) {
    companion object {
        fun zero(): CoffeeChatCountPerCategory = CoffeeChatCountPerCategory()
    }
}
