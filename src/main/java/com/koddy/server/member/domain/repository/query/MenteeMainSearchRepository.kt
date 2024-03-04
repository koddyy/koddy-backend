package com.koddy.server.member.domain.repository.query

import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.query.response.SuggestedCoffeeChatsByMentor
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface MenteeMainSearchRepository {
    fun fetchSuggestedMentors(
        menteeId: Long,
        limit: Int,
    ): Page<SuggestedCoffeeChatsByMentor>

    fun fetchMentorsByCondition(
        condition: SearchMentorCondition,
        pageable: Pageable,
    ): Slice<Mentor>
}
