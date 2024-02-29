package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.query.PageCreator
import com.koddy.server.global.query.PageResponse
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.member.application.usecase.query.GetSuggestedMentors
import com.koddy.server.member.application.usecase.query.LookAroundMentorsByConditionQuery
import com.koddy.server.member.application.usecase.query.response.MentorSimpleSearchProfile
import com.koddy.server.member.application.usecase.query.response.SuggestedCoffeeChatsByMentorResponse
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.query.MenteeMainSearchRepository
import com.koddy.server.member.domain.repository.query.response.SuggestedCoffeeChatsByMentor
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

@UseCase
class MenteeMainSearchUseCase(
    private val menteeMainSearchRepository: MenteeMainSearchRepository,
) {
    @KoddyReadOnlyTransactional
    fun getSuggestedMentors(query: GetSuggestedMentors): PageResponse<List<SuggestedCoffeeChatsByMentorResponse>> {
        val result: Page<SuggestedCoffeeChatsByMentor> = menteeMainSearchRepository.fetchSuggestedMentors(query.menteeId, query.limit)
        return PageResponse(
            result.content.map { SuggestedCoffeeChatsByMentorResponse.from(it) },
            result.totalElements,
            result.hasNext(),
        )
    }

    @KoddyReadOnlyTransactional
    fun lookAroundMentorsByCondition(query: LookAroundMentorsByConditionQuery): SliceResponse<List<MentorSimpleSearchProfile>> {
        val condition: SearchMentorCondition = query.toCondition()
        val pageable: Pageable = PageCreator.create(query.page)
        val result: Slice<Mentor> = menteeMainSearchRepository.fetchMentorsByCondition(condition, pageable)

        return SliceResponse(
            result.content.map { MentorSimpleSearchProfile.from(it) },
            result.hasNext(),
        )
    }
}
