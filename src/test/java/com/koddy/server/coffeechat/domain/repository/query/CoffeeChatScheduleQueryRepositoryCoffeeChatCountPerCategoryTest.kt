package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import

@RepositoryTestKt
@Import(CoffeeChatScheduleQueryRepositoryImpl::class)
@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryRepository 테스트")
internal class CoffeeChatScheduleQueryRepositoryCoffeeChatCountPerCategoryTest(
    private val sut: CoffeeChatScheduleQueryRepositoryImpl,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    private lateinit var mentees: List<Mentee>
    private lateinit var mentors: List<Mentor>

    @BeforeEach
    fun setUp() {
        mentees = mutableListOf<Mentee>().apply {
            (1..7).forEach { add(memberRepository.save(menteeFixture(sequence = it).toDomain())) }
        }
        mentors = mutableListOf<Mentor>().apply {
            (1..7).forEach { add(memberRepository.save(mentorFixture(sequence = it).toDomain())) }
        }
    }

    @Test
    @DisplayName("멘토의 카테고리별 커피챗 개수를 조회한다")
    fun fetchMentorCoffeeChatCountPerCategory() {
        // 대기 = 0, 제안 = 0, 예정 = 0, 지나간 = 0
        val result1: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result1, listOf(0, 0, 0, 0))

        // 대기 = 0, 제안 = 1, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggest(mentor = mentors[0], mentee = mentees[0]))
        val result2: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result2, listOf(0, 1, 0, 0))

        // 대기 = 0, 제안 = 2, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggest(mentor = mentors[0], mentee = mentees[1]))
        val result3: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result3, listOf(0, 2, 0, 0))

        // 대기 = 1, 제안 = 2, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggestAndPending(fixture = 월요일_1주차_20_00_시작, mentor = mentors[0], mentee = mentees[2]))
        val result4: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result4, listOf(1, 2, 0, 0))

        // 대기 = 2, 제안 = 2, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MenteeFlow.apply(fixture = 월요일_2주차_20_00_시작, mentee = mentees[3], mentor = mentors[0]))
        val result5: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result5, listOf(2, 2, 0, 0))

        // 대기 = 2, 제안 = 2, 예정 = 1, 지나간 = 0
        coffeeChatRepository.save(MenteeFlow.applyAndApprove(fixture = 월요일_3주차_20_00_시작, mentee = mentees[4], mentor = mentors[0]))
        val result6: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result6, listOf(2, 2, 1, 0))

        // 대기 = 2, 제안 = 2, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.applyAndComplete(fixture = 월요일_4주차_20_00_시작, mentee = mentees[5], mentor = mentors[0]))
        val result7: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result7, listOf(2, 2, 1, 1))

        // 대기 = 2, 제안 = 2, 예정 = 2, 지나간 = 1
        coffeeChatRepository.save(MentorFlow.suggestAndFinallyApprove(fixture = 수요일_1주차_20_00_시작, mentor = mentors[0], mentee = mentees[6]))
        val result8: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertCoffeeChatCountMatch(result8, listOf(2, 2, 2, 1))
    }

    @Test
    @DisplayName("멘티의 카테고리별 커피챗 개수를 조회한다")
    fun fetchMenteeCoffeeChatCountPerCategory() {
        // 대기 = 0, 제안 = 0, 예정 = 0, 지나간 = 0
        val result1: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result1, listOf(0, 0, 0, 0))

        // 대기 = 1, 제안 = 0, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggestAndPending(fixture = 월요일_1주차_20_00_시작, mentor = mentors[0], mentee = mentees[0]))
        val result2: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result2, listOf(1, 0, 0, 0))

        // 대기 = 1, 제안 = 0, 예정 = 1, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggestAndFinallyApprove(fixture = 월요일_2주차_20_00_시작, mentor = mentors[1], mentee = mentees[0]))
        val result3: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result3, listOf(1, 0, 1, 0))

        // 대기 = 2, 제안 = 0, 예정 = 1, 지나간 = 0
        coffeeChatRepository.save(MenteeFlow.apply(fixture = 월요일_3주차_20_00_시작, mentee = mentees[0], mentor = mentors[2]))
        val result4: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result4, listOf(2, 0, 1, 0))

        // 대기 = 2, 제안 = 0, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.applyAndComplete(fixture = 월요일_4주차_20_00_시작, mentee = mentees[0], mentor = mentors[3]))
        val result5: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result5, listOf(2, 0, 1, 1))

        // 대기 = 3, 제안 = 0, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.apply(fixture = 수요일_1주차_20_00_시작, mentee = mentees[0], mentor = mentors[4]))
        val result6: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result6, listOf(3, 0, 1, 1))

        // 대기 = 4, 제안 = 0, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MentorFlow.suggestAndPending(fixture = 수요일_1주차_20_00_시작, mentor = mentors[5], mentee = mentees[0]))
        val result7: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result7, listOf(4, 0, 1, 1))

        // 대기 = 4, 제안 = 0, 예정 = 2, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.applyAndApprove(fixture = 월요일_4주차_20_00_시작, mentee = mentees[0], mentor = mentors[6]))
        val result8: CoffeeChatCountPerCategory = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].id)
        assertCoffeeChatCountMatch(result8, listOf(4, 0, 2, 1))
    }

    private fun assertCoffeeChatCountMatch(
        result: CoffeeChatCountPerCategory,
        counts: List<Int>,
    ) {
        assertSoftly(result) {
            waiting shouldBe counts[0]
            suggested shouldBe counts[1]
            scheduled shouldBe counts[2]
            passed shouldBe counts[3]
        }
    }
}
