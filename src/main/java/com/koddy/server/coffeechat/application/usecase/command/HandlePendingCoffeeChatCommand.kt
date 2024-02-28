package com.koddy.server.coffeechat.application.usecase.command

import com.koddy.server.coffeechat.domain.model.Strategy

class FinallyCancelPendingCoffeeChatCommand(
    val mentorId: Long,
    val coffeeChatId: Long,
    val cancelReason: String,
)

data class FinallyApprovePendingCoffeeChatCommand(
    val mentorId: Long,
    val coffeeChatId: Long,
    val type: Strategy.Type,
    val value: String,
)
