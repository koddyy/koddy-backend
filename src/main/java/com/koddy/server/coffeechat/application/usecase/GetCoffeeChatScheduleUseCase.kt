package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats
import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats
import com.koddy.server.coffeechat.application.usecase.query.response.CoffeeChatEachCategoryCounts
import com.koddy.server.coffeechat.domain.repository.query.CoffeeChatScheduleQueryRepository
import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.query.PageCreator
import com.koddy.server.global.query.SliceResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

@UseCase
class GetCoffeeChatScheduleUseCase(
    private val coffeeChatScheduleQueryRepository: CoffeeChatScheduleQueryRepository,
) {
    fun getEachCategoryCounts(authenticated: Authenticated): CoffeeChatEachCategoryCounts {
        val result: CoffeeChatCountPerCategory = when (authenticated.isMentor) {
            true -> coffeeChatScheduleQueryRepository.fetchMentorCoffeeChatCountPerCategory(authenticated.id)
            false -> coffeeChatScheduleQueryRepository.fetchMenteeCoffeeChatCountPerCategory(authenticated.id)
        }
        return CoffeeChatEachCategoryCounts.from(result)
    }

    fun getMentorSchedules(query: GetMentorCoffeeChats): SliceResponse<List<MentorCoffeeChatScheduleData>> {
        val condition = MentorCoffeeChatQueryCondition(query.mentorId, query.status)
        val pageable: Pageable = PageCreator.create(query.page)

        val result: Slice<MentorCoffeeChatScheduleData> = coffeeChatScheduleQueryRepository.fetchMentorCoffeeChatSchedules(condition, pageable)
        return SliceResponse(
            result = result.content,
            hasNext = result.hasNext(),
        )
    }

    fun getMenteeSchedules(query: GetMenteeCoffeeChats): SliceResponse<List<MenteeCoffeeChatScheduleData>> {
        val condition = MenteeCoffeeChatQueryCondition(query.menteeId, query.status)
        val pageable: Pageable = PageCreator.create(query.page)

        val result: Slice<MenteeCoffeeChatScheduleData> = coffeeChatScheduleQueryRepository.fetchMenteeCoffeeChatSchedules(condition, pageable)
        return SliceResponse(
            result = result.content,
            hasNext = result.hasNext(),
        )
    }
}
