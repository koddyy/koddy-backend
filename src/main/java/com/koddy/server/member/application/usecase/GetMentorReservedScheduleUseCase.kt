package com.koddy.server.member.application.usecase

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.query.GetMentorReservedSchedule
import com.koddy.server.member.application.usecase.query.response.MentorReservedSchedule
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MentorRepository

@UseCase
class GetMentorReservedScheduleUseCase(
    private val mentorRepository: MentorRepository,
    private val mentorReservedScheduleQueryRepository: MentorReservedScheduleQueryRepository,
) {
    @KoddyReadOnlyTransactional
    fun invoke(query: GetMentorReservedSchedule): MentorReservedSchedule {
        val mentor: Mentor = mentorRepository.getByIdWithSchedules(query.mentorId)
        val reservedCoffeeChat: List<CoffeeChat> = mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(
            query.mentorId,
            query.year,
            query.month,
        )
        return MentorReservedSchedule.of(mentor, reservedCoffeeChat)
    }
}
