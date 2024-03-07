package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat

interface MentorReservedScheduleQueryRepository {
    fun fetchReservedCoffeeChat(
        mentorId: Long,
        year: Int,
        month: Int,
    ): List<CoffeeChat>
}
