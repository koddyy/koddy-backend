package com.koddy.server.coffeechat.domain.repository.query.spec

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus

data class MenteeCoffeeChatQueryCondition(
    val menteeId: Long,
    val status: List<CoffeeChatStatus>,
)
