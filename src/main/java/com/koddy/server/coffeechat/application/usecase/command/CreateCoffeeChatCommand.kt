package com.koddy.server.coffeechat.application.usecase.command

import com.koddy.server.coffeechat.domain.model.Reservation

data class CreateCoffeeChatByApplyCommand(
    val menteeId: Long,
    val mentorId: Long,
    val applyReason: String,
    val reservation: Reservation,
)

data class CreateCoffeeChatBySuggestCommand(
    val mentorId: Long,
    val menteeId: Long,
    val suggestReason: String,
)
