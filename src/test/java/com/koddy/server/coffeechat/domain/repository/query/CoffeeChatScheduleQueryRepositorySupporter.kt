package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.global.query.PageCreator
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import org.springframework.data.domain.Pageable

@RepositoryTestKt
abstract class CoffeeChatScheduleQueryRepositorySupporter(
    private val memberRepository: MemberRepository,
) {
    protected lateinit var mentees: MutableList<Mentee>
    protected lateinit var mentors: MutableList<Mentor>
    protected val pageable1: Pageable = PageCreator.create(1)
    protected val pageable2: Pageable = PageCreator.create(2)

    protected fun initMembers() {
        mentees = mutableListOf<Mentee>().apply {
            (1..20).forEach { add(memberRepository.save(menteeFixture(sequence = it).toDomain())) }
        }
        mentors = mutableListOf<Mentor>().apply {
            (1..20).forEach { add(memberRepository.save(mentorFixture(sequence = it).toDomain())) }
        }
    }
}
