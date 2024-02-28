package com.koddy.server.coffeechat.application.usecase.command

import com.koddy.server.coffeechat.domain.model.Strategy

data class RejectAppliedCoffeeChatCommand(
    val mentorId: Long,
    val coffeeChatId: Long,
    val rejectReason: String,
)

data class ApproveAppliedCoffeeChatCommand(
    val mentorId: Long,
    val coffeeChatId: Long,
    val question: String,
    val type: Strategy.Type,
    val value: String,
)
