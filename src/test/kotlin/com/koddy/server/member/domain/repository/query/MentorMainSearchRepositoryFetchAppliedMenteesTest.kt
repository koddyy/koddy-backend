package com.koddy.server.member.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_21_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_21_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_21_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_21_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_21_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page

@RepositoryTestKt
@Import(MentorMainSearchRepositoryImpl::class)
@DisplayName("Member -> MentorMainSearchRepository [fetchAppliedCoffeeChatsByMentee] 테스트")
internal class MentorMainSearchRepositoryFetchAppliedMenteesTest(
    private val sut: MentorMainSearchRepositoryImpl,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    private lateinit var mentor: Mentor
    private lateinit var mentees: List<Mentee>

    @BeforeEach
    fun setUp() {
        mentor = memberRepository.save(mentorFixture(sequence = 1).toDomain())
        mentees = mutableListOf<Mentee>().apply {
            (1..10).forEach { add(memberRepository.save(menteeFixture(sequence = it).toDomain())) }
        }
    }

    @Test
    fun `멘토 자신에게 커피챗을 신청한 멘티를 limit 개수만큼 최근에 신청한 순서대로 조회한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.apply(fixture = 월요일_1주차_20_00_시작, mentee = mentees[0], mentor = mentor),
                MenteeFlow.apply(fixture = 월요일_1주차_21_00_시작, mentee = mentees[1], mentor = mentor),
                MenteeFlow.apply(fixture = 월요일_2주차_20_00_시작, mentee = mentees[2], mentor = mentor),
                MenteeFlow.apply(fixture = 월요일_2주차_21_00_시작, mentee = mentees[3], mentor = mentor),
                MenteeFlow.apply(fixture = 월요일_3주차_20_00_시작, mentee = mentees[4], mentor = mentor),
                MenteeFlow.apply(fixture = 월요일_3주차_21_00_시작, mentee = mentees[5], mentor = mentor),
                MenteeFlow.apply(fixture = 월요일_4주차_20_00_시작, mentee = mentees[6], mentor = mentor),
                MenteeFlow.apply(fixture = 월요일_4주차_21_00_시작, mentee = mentees[7], mentor = mentor),
                MenteeFlow.apply(fixture = 수요일_1주차_20_00_시작, mentee = mentees[8], mentor = mentor),
                MenteeFlow.apply(fixture = 수요일_1주차_21_00_시작, mentee = mentees[9], mentor = mentor),
            ),
        )

        /* limit별 조회 */
        val result1: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 3)
        val result2: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 5)
        val result3: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 7)
        val result4: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 10)

        assertSoftly {
            result1.hasNext() shouldBe true
            result1.totalElements shouldBe 10
            result1.content.map { it.coffeeChatId } shouldContainExactly listOf(coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id)
            result1.content.map { it.menteeId } shouldContainExactly listOf(mentees[9].id, mentees[8].id, mentees[7].id)
            result2.hasNext() shouldBe true
            result2.totalElements shouldBe 10
            result2.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id,
                coffeeChats[6].id, coffeeChats[5].id,
            )
            result2.content.map { it.menteeId } shouldContainExactly listOf(
                mentees[9].id, mentees[8].id, mentees[7].id,
                mentees[6].id, mentees[5].id,
            )
            result3.hasNext() shouldBe true
            result3.totalElements shouldBe 10
            result3.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id, coffeeChats[6].id,
                coffeeChats[5].id, coffeeChats[4].id, coffeeChats[3].id,
            )
            result3.content.map { it.menteeId } shouldContainExactly listOf(
                mentees[9].id, mentees[8].id, mentees[7].id, mentees[6].id,
                mentees[5].id, mentees[4].id, mentees[3].id,
            )
            result4.hasNext() shouldBe false
            result4.totalElements shouldBe 10
            result4.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id, coffeeChats[6].id, coffeeChats[5].id,
                coffeeChats[4].id, coffeeChats[3].id, coffeeChats[2].id, coffeeChats[1].id, coffeeChats[0].id,
            )
            result4.content.map { it.menteeId } shouldContainExactly listOf(
                mentees[9].id, mentees[8].id, mentees[7].id, mentees[6].id, mentees[5].id,
                mentees[4].id, mentees[3].id, mentees[2].id, mentees[1].id, mentees[0].id,
            )
        }

        /* cancel 후 limit별 조회 */
        coffeeChats[3].cancel(status = CANCEL_FROM_MENTEE_FLOW, cancelBy = mentees[3].id, cancelReason = "취소..")
        coffeeChats[5].cancel(status = CANCEL_FROM_MENTEE_FLOW, cancelBy = mentees[5].id, cancelReason = "취소..")
        coffeeChats[7].cancel(status = CANCEL_FROM_MENTEE_FLOW, cancelBy = mentees[7].id, cancelReason = "취소..")
        coffeeChats[9].cancel(status = CANCEL_FROM_MENTEE_FLOW, cancelBy = mentees[9].id, cancelReason = "취소..")

        val result5: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 3)
        val result6: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 5)
        val result7: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 7)
        val result8: Page<AppliedCoffeeChatsByMentee> = sut.fetchAppliedMentees(mentorId = mentor.id, limit = 10)

        assertSoftly {
            result5.hasNext() shouldBe true
            result5.totalElements shouldBe 6
            result5.content.map { it.coffeeChatId } shouldContainExactly listOf(coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id)
            result5.content.map { it.menteeId } shouldContainExactly listOf(mentees[8].id, mentees[6].id, mentees[4].id)
            result6.hasNext() shouldBe true
            result6.totalElements shouldBe 6
            result6.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id,
                coffeeChats[2].id, coffeeChats[1].id,
            )
            result6.content.map { it.menteeId } shouldContainExactly listOf(
                mentees[8].id, mentees[6].id, mentees[4].id,
                mentees[2].id, mentees[1].id,
            )
            result7.hasNext() shouldBe false
            result7.totalElements shouldBe 6
            result7.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id,
                coffeeChats[2].id, coffeeChats[1].id, coffeeChats[0].id,
            )
            result7.content.map { it.menteeId } shouldContainExactly listOf(
                mentees[8].id, mentees[6].id, mentees[4].id,
                mentees[2].id, mentees[1].id, mentees[0].id,
            )
            result8.hasNext() shouldBe false
            result8.totalElements shouldBe 6
            result8.content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[8].id, coffeeChats[6].id, coffeeChats[4].id,
                coffeeChats[2].id, coffeeChats[1].id, coffeeChats[0].id,
            )
            result8.content.map { it.menteeId } shouldContainExactly listOf(
                mentees[8].id, mentees[6].id, mentees[4].id,
                mentees[2].id, mentees[1].id, mentees[0].id,
            )
        }
    }
}
