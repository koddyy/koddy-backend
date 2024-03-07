package com.koddy.server.member.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.query.response.SuggestedCoffeeChatsByMentor
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page

@RepositoryTestKt
@Import(MenteeMainSearchRepositoryImpl::class)
@DisplayName("Member -> MenteeMainSearchRepository [fetchSuggestedCoffeeChatsByMentor] 테스트")
internal class MenteeMainSearchRepositoryFetchSuggestedMentorsTest(
    private val sut: MenteeMainSearchRepositoryImpl,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    private lateinit var mentee: Mentee
    private lateinit var mentors: List<Mentor>

    @BeforeEach
    fun setUp() {
        mentee = memberRepository.save(menteeFixture(sequence = 1).toDomain())
        mentors = mutableListOf<Mentor>().apply {
            (1..10).forEach { add(memberRepository.save(mentorFixture(sequence = it).toDomain())) }
        }
    }

    @Test
    fun `멘티 자신에게 커피챗을 제안한 멘토를 limit 개수만큼 최근에 신청한 순서대로 조회한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MentorFlow.suggest(mentor = mentors[0], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[1], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[2], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[3], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[4], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[5], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[6], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[7], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[8], mentee = mentee),
                MentorFlow.suggest(mentor = mentors[9], mentee = mentee),
            ),
        )

        /* limit별 조회 */
        val result1: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 3)
        val result2: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 5)
        val result3: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 7)
        val result4: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 10)

        assertSoftly {
            result1.hasNext() shouldBe true
            result1.totalElements shouldBe 10
            result1.content.map { it.coffeeChatId } shouldContainExactly listOf(coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id)
            result1.content.map { it.mentorId } shouldContainExactly listOf(mentors[9].id, mentors[8].id, mentors[7].id)
            result2.hasNext() shouldBe true
            result2.totalElements shouldBe 10
            result2.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id,
                coffeeChats[6].id, coffeeChats[5].id,
            )
            result2.content.map { it.mentorId } shouldContainExactly listOf(
                mentors[9].id, mentors[8].id, mentors[7].id,
                mentors[6].id, mentors[5].id,
            )
            result3.hasNext() shouldBe true
            result3.totalElements shouldBe 10
            result3.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id, coffeeChats[6].id,
                coffeeChats[5].id, coffeeChats[4].id, coffeeChats[3].id,
            )
            result3.content.map { it.mentorId } shouldContainExactly listOf(
                mentors[9].id, mentors[8].id, mentors[7].id, mentors[6].id,
                mentors[5].id, mentors[4].id, mentors[3].id,
            )
            result4.hasNext() shouldBe false
            result4.totalElements shouldBe 10
            result4.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id, coffeeChats[6].id, coffeeChats[5].id,
                coffeeChats[4].id, coffeeChats[3].id, coffeeChats[2].id, coffeeChats[1].id, coffeeChats[0].id,
            )
            result4.content.map { it.mentorId } shouldContainExactly listOf(
                mentors[9].id, mentors[8].id, mentors[7].id, mentors[6].id, mentors[5].id,
                mentors[4].id, mentors[3].id, mentors[2].id, mentors[1].id, mentors[0].id,
            )
        }

        /* cancel 후 limit별 조회 */
        coffeeChats[3].cancel(status = CANCEL_FROM_MENTOR_FLOW, cancelBy = mentors[3].id, cancelReason = "취소..")
        coffeeChats[5].cancel(status = CANCEL_FROM_MENTOR_FLOW, cancelBy = mentors[5].id, cancelReason = "취소..")
        coffeeChats[7].cancel(status = CANCEL_FROM_MENTOR_FLOW, cancelBy = mentors[7].id, cancelReason = "취소..")
        coffeeChats[9].cancel(status = CANCEL_FROM_MENTOR_FLOW, cancelBy = mentors[9].id, cancelReason = "취소..")

        val result5: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 3)
        val result6: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 5)
        val result7: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 7)
        val result8: Page<SuggestedCoffeeChatsByMentor> = sut.fetchSuggestedMentors(menteeId = mentee.id, limit = 10)

        assertSoftly {
            result5.hasNext() shouldBe true
            result5.totalElements shouldBe 6
            result5.content.map { it.coffeeChatId } shouldContainExactly listOf(coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id)
            result5.content.map { it.mentorId } shouldContainExactly listOf(mentors[8].id, mentors[6].id, mentors[4].id)
            result6.hasNext() shouldBe true
            result6.totalElements shouldBe 6
            result6.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id,
                coffeeChats[2].id, coffeeChats[1].id,
            )
            result6.content.map { it.mentorId } shouldContainExactly listOf(
                mentors[8].id, mentors[6].id, mentors[4].id,
                mentors[2].id, mentors[1].id,
            )
            result7.hasNext() shouldBe false
            result7.totalElements shouldBe 6
            result7.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id,
                coffeeChats[2].id, coffeeChats[1].id, coffeeChats[0].id,
            )
            result7.content.map { it.mentorId } shouldContainExactly listOf(
                mentors[8].id, mentors[6].id, mentors[4].id,
                mentors[2].id, mentors[1].id, mentors[0].id,
            )
            result8.hasNext() shouldBe false
            result8.totalElements shouldBe 6
            result8.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id,
                coffeeChats[2].id, coffeeChats[1].id, coffeeChats[0].id,
            )
            result8.content.map { it.mentorId } shouldContainExactly listOf(
                mentors[8].id, mentors[6].id, mentors[4].id,
                mentors[2].id, mentors[1].id, mentors[0].id,
            )
        }
    }
}
