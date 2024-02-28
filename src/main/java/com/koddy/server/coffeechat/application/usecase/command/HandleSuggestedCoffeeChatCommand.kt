package com.koddy.server.coffeechat.application.usecase.command

import com.koddy.server.coffeechat.domain.model.Reservation

data class RejectSuggestedCoffeeChatCommand(
    val menteeId: Long,
    val coffeeChatId: Long,
    val rejectReason: String,
)

data class PendingSuggestedCoffeeChatCommand(
    val menteeId: Long,
    val coffeeChatId: Long,
    val question: String,
    val reservation: Reservation,
)
