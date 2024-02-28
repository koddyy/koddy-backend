package com.koddy.server.coffeechat.application.usecase.query

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus

data class GetMenteeCoffeeChats(
    val menteeId: Long,
    val status: List<CoffeeChatStatus>,
    val page: Int,
)
