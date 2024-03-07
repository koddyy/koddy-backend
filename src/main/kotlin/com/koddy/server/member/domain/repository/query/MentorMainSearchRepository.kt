package com.koddy.server.member.domain.repository.query

import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface MentorMainSearchRepository {
    fun fetchAppliedMentees(
        mentorId: Long,
        limit: Int,
    ): Page<AppliedCoffeeChatsByMentee>

    fun fetchMenteesByCondition(
        condition: SearchMenteeCondition,
        pageable: Pageable,
    ): Slice<Mentee>
}
