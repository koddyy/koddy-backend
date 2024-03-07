package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.COFFEE_CHAT_NOT_FOUND
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CoffeeChatReader(
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    fun getById(id: Long): CoffeeChat {
        return coffeeChatRepository.findByIdOrNull(id)
            ?: throw CoffeeChatException(COFFEE_CHAT_NOT_FOUND)
    }

    fun getByMentor(
        id: Long,
        mentorId: Long,
    ): CoffeeChat {
        return coffeeChatRepository.findByIdAndMentorId(id, mentorId)
            ?: throw CoffeeChatException(COFFEE_CHAT_NOT_FOUND)
    }

    fun getByMentee(
        id: Long,
        menteeId: Long,
    ): CoffeeChat {
        return coffeeChatRepository.findByIdAndMenteeId(id, menteeId)
            ?: throw CoffeeChatException(COFFEE_CHAT_NOT_FOUND)
    }

    fun findIdsByStatusAndPassedDeadline(
        status: CoffeeChatStatus,
        deadline: LocalDateTime,
    ): List<Long> {
        return coffeeChatRepository.findIdsByStatusAndPassedDeadline(status, deadline)
    }

    fun getMentorCoffeeChatCountByStatus(
        mentorId: Long,
        status: List<CoffeeChatStatus>,
    ): Long {
        return coffeeChatRepository.countByMentorIdAndStatusIn(mentorId, status)
    }

    fun getMenteeCoffeeChatCountByStatus(
        menteeId: Long,
        status: List<CoffeeChatStatus>,
    ): Long {
        return coffeeChatRepository.countByMenteeIdAndStatusIn(menteeId, status)
    }
}
