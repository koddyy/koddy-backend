package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface CoffeeChatScheduleQueryRepository {
    /**
     * 멘토 카테고리별 커피챗 개수
     */
    fun fetchMentorCoffeeChatCountPerCategory(mentorId: Long): CoffeeChatCountPerCategory

    /**
     * 멘티 카테고리별 커피챗 개수
     */
    fun fetchMenteeCoffeeChatCountPerCategory(menteeId: Long): CoffeeChatCountPerCategory

    /**
     * 멘토 내 일정
     */
    fun fetchMentorCoffeeChatSchedules(
        condition: MentorCoffeeChatQueryCondition,
        pageable: Pageable,
    ): Slice<MentorCoffeeChatScheduleData>

    /**
     * 멘티 내 일정
     */
    fun fetchMenteeCoffeeChatSchedules(
        condition: MenteeCoffeeChatQueryCondition,
        pageable: Pageable,
    ): Slice<MenteeCoffeeChatScheduleData>
}
