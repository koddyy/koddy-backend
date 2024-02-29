package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.global.query.PageCreator
import com.koddy.server.global.query.PageResponse
import com.koddy.server.global.query.SliceResponse
import com.koddy.server.member.application.usecase.query.GetAppliedMentees
import com.koddy.server.member.application.usecase.query.LookAroundMenteesByConditionQuery
import com.koddy.server.member.application.usecase.query.response.AppliedCoffeeChatsByMenteeResponse
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.query.MentorMainSearchRepository
import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

@UseCase
class MentorMainSearchUseCase(
    private val mentorMainSearchRepository: MentorMainSearchRepository,
) {
    @KoddyReadOnlyTransactional
    fun getAppliedMentees(query: GetAppliedMentees): PageResponse<List<AppliedCoffeeChatsByMenteeResponse>> {
        val result: Page<AppliedCoffeeChatsByMentee> = mentorMainSearchRepository.fetchAppliedMentees(query.mentorId, query.limit)
        return PageResponse(
            result.content.map { AppliedCoffeeChatsByMenteeResponse.from(it) },
            result.totalElements,
            result.hasNext(),
        )
    }

    @KoddyReadOnlyTransactional
    fun lookAroundMenteesByCondition(query: LookAroundMenteesByConditionQuery): SliceResponse<List<MenteeSimpleSearchProfile>> {
        val condition: SearchMenteeCondition = query.toCondition()
        val pageable: Pageable = PageCreator.create(query.page)
        val result: Slice<Mentee> = mentorMainSearchRepository.fetchMenteesByCondition(condition, pageable)

        return SliceResponse(
            result.content.map { MenteeSimpleSearchProfile.from(it) },
            result.hasNext(),
        )
    }
}
