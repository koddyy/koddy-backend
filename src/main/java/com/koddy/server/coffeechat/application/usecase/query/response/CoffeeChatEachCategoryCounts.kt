package com.koddy.server.coffeechat.application.usecase.query.response

import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory

data class CoffeeChatEachCategoryCounts(
    val waiting: Long,
    val suggested: Long,
    val scheduled: Long,
    val passed: Long,
) {
    companion object {
        fun from(query: CoffeeChatCountPerCategory): CoffeeChatEachCategoryCounts =
            CoffeeChatEachCategoryCounts(
                waiting = query.waiting,
                suggested = query.suggested,
                scheduled = query.scheduled,
                passed = query.passed,
            )
    }
}
