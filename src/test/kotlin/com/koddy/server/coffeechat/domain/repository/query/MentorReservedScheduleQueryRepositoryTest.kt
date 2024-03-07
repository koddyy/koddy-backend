package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import

@RepositoryTestKt
@Import(MentorReservedScheduleQueryRepositoryImpl::class)
@DisplayName("CoffeeChat - mentorReservedScheduleQueryRepository 테스트")
internal class MentorReservedScheduleQueryRepositoryTest(
    private val sut: MentorReservedScheduleQueryRepositoryImpl,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    /**
     * - mentees[0] 제안 + 1차 수락 (2024-02-05) = PENTING
     * - mentees[1] 제안 = SUGGEST
     * - mentees[2] 제안 + 거절 = REJECT
     * - mentees[3] 신청 (2024-02-19) = APPLY
     * - mentees[4] 신청 (2024-03-04) + 수락 = APPROVE
     * - mentees[5] 제안 + 1차 수락 (2024-03-15) + 최종 수락 = APPROVE
     * - mentees[6] 제안 + 1차 수락 (2024-04-01) + 거절 = REJECT
     * - mentees[7] 신청 (2024-04-05) = APPLY
     * - mentees[8] 신청 (2024-04-17) + 수락 = APPROVE
     * - mentees[9] 제안 + 1차 수락 (2024-04-10) = PENDING
     */
    @Test
    fun `특정 Year-Month에 예약된 커피챗을 조회한다 (MenteeFlow=MENTEE_APPLY, MENTOR_APPROVE & MentorFlow=MENTEE_PENDING, MENTOR_FINALLY_APPROVE)`() {
        // given
        val mentor: Mentor = memberRepository.save(mentorFixture(sequence = 1).toDomain())
        val mentees: List<Mentee> = memberRepository.saveAll(
            listOf(
                menteeFixture(sequence = 1).toDomain(),
                menteeFixture(sequence = 2).toDomain(),
                menteeFixture(sequence = 3).toDomain(),
                menteeFixture(sequence = 4).toDomain(),
                menteeFixture(sequence = 5).toDomain(),
                menteeFixture(sequence = 6).toDomain(),
                menteeFixture(sequence = 7).toDomain(),
                menteeFixture(sequence = 8).toDomain(),
                menteeFixture(sequence = 9).toDomain(),
                menteeFixture(sequence = 10).toDomain(),
            ),
        )

        val startLines = listOf(
            "2024/2/5-18:00".toLocalDateTime(),
            "2024/2/19-18:00".toLocalDateTime(),
            "2024/3/4-18:00".toLocalDateTime(),
            "2024/3/15-18:00".toLocalDateTime(),
            "2024/4/1-18:00".toLocalDateTime(),
            "2024/4/17-18:00".toLocalDateTime(),
            "2024/4/5-18:00".toLocalDateTime(),
            "2024/4/10-18:00".toLocalDateTime(),
        )
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MentorFlow.suggestAndPending(start = startLines[0], end = startLines[0].plusMinutes(30), mentor = mentor, mentee = mentees[0]),
                MentorFlow.suggest(mentor = mentor, mentee = mentees[1]),
                MentorFlow.suggestAndReject(mentor = mentor, mentee = mentees[2]),
                MenteeFlow.apply(start = startLines[1], end = startLines[1].plusMinutes(30), mentee = mentees[3], mentor = mentor),
                MenteeFlow.applyAndApprove(start = startLines[2], end = startLines[2].plusMinutes(30), mentee = mentees[4], mentor = mentor),
                MentorFlow.suggestAndPending(start = startLines[3], end = startLines[3].plusMinutes(30), mentor = mentor, mentee = mentees[5]),
                MentorFlow.suggestAndFinallyCancel(start = startLines[4], end = startLines[4].plusMinutes(30), mentor = mentor, mentee = mentees[6]),
                MenteeFlow.apply(start = startLines[5], end = startLines[5].plusMinutes(30), mentee = mentees[7], mentor = mentor),
                MenteeFlow.applyAndApprove(start = startLines[6], end = startLines[6].plusMinutes(30), mentee = mentees[8], mentor = mentor),
                MentorFlow.suggestAndPending(start = startLines[7], end = startLines[7].plusMinutes(30), mentor = mentor, mentee = mentees[9]),
            ),
        )

        // when
        val result1: List<CoffeeChat> = sut.fetchReservedCoffeeChat(mentorId = mentor.id, year = 2024, month = 1)
        val result2: List<CoffeeChat> = sut.fetchReservedCoffeeChat(mentorId = mentor.id, year = 2024, month = 2)
        val result3: List<CoffeeChat> = sut.fetchReservedCoffeeChat(mentorId = mentor.id, year = 2024, month = 3)
        val result4: List<CoffeeChat> = sut.fetchReservedCoffeeChat(mentorId = mentor.id, year = 2024, month = 4)
        val result5: List<CoffeeChat> = sut.fetchReservedCoffeeChat(mentorId = mentor.id, year = 2024, month = 5)

        assertSoftly {
            result1 shouldBe emptyList()
            result2 shouldContainExactly listOf(coffeeChats[0], coffeeChats[3])
            result3 shouldContainExactly listOf(coffeeChats[4], coffeeChats[5])
            result4 shouldContainExactly listOf(coffeeChats[8], coffeeChats[9], coffeeChats[7])
            result5 shouldBe emptyList()
        }
    }
}
