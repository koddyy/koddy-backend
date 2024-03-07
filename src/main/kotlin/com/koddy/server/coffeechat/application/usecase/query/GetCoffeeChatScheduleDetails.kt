package com.koddy.server.coffeechat.application.usecase.query

import com.koddy.server.auth.domain.model.Authenticated

data class GetCoffeeChatScheduleDetails(
    val authenticated: Authenticated,
    val coffeeChatId: Long,
)
