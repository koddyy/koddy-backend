package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import org.springframework.stereotype.Service

@Service
class CoffeeChatWriter(
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    fun save(coffeeChat: CoffeeChat): CoffeeChat {
        return coffeeChatRepository.save(coffeeChat)
    }

    fun updateStatusInBatch(
        ids: List<Long>,
        status: CoffeeChatStatus,
    ) {
        return coffeeChatRepository.updateStatusInBatch(ids, status)
    }
}
