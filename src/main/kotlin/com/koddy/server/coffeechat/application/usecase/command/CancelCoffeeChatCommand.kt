package com.koddy.server.coffeechat.application.usecase.command

import com.koddy.server.auth.domain.model.Authenticated

data class CancelCoffeeChatCommand(
    val authenticated: Authenticated,
    val coffeeChatId: Long,
    val cancelReason: String,
)
