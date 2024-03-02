package com.koddy.server.coffeechat.domain.repository

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_4주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_3
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_4
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_5
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@RepositoryTestKt
@DisplayName("CoffeeChat -> CoffeeChatRepository 테스트")
internal class CoffeeChatRepositoryTest(
    private val sut: CoffeeChatRepository,
    private val memberRepository: MemberRepository,
) {
    @Test
    fun `Status에 해당되고 Deadline을 지나간 커피챗 ID를 조회한다 - findIdsByStatusAndPassedDeadline`() {
        // given
        val mentees: List<Mentee> = memberRepository.saveAll(
            listOf(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
                MENTEE_3.toDomain(),
                MENTEE_4.toDomain(),
                MENTEE_5.toDomain(),
            ),
        )
        val mentors: List<Mentor> = memberRepository.saveAll(
            listOf(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain(),
            ),
        )
        val coffeeChats: List<CoffeeChat> = sut.saveAll(
            listOf(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                MenteeFlow.applyAndReject(월요일_3주차_20_00_시작, mentees[2], mentors[0]),
                MenteeFlow.applyAndCancel(월요일_4주차_20_00_시작, mentees[3], mentors[0]),
                MenteeFlow.applyAndApprove(수요일_1주차_20_00_시작, mentees[4], mentors[0]),
                MentorFlow.suggest(mentors[1], mentees[0]),
                MentorFlow.suggest(mentors[1], mentees[1]),
                MentorFlow.suggestAndPending(수요일_2주차_20_00_시작, mentors[1], mentees[2]),
                MentorFlow.suggestAndPending(수요일_3주차_20_00_시작, mentors[1], mentees[3]),
                MentorFlow.suggestAndFinallyApprove(수요일_4주차_20_00_시작, mentors[1], mentees[4]),
            ),
        )
        val now = LocalDateTime.now()

        // when - then
        assertSoftly {
            sut.findIdsByStatusAndPassedDeadline(MENTEE_APPLY, 월요일_1주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[0].id)
            sut.findIdsByStatusAndPassedDeadline(MENTEE_APPLY, 월요일_2주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[0].id, coffeeChats[1].id)
            sut.findIdsByStatusAndPassedDeadline(MENTOR_REJECT, 월요일_3주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[2].id)
            sut.findIdsByStatusAndPassedDeadline(MENTOR_APPROVE, 수요일_1주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[4].id)
            sut.findIdsByStatusAndPassedDeadline(MENTEE_APPLY_COFFEE_CHAT_COMPLETE, now) shouldBe emptyList()
            sut.findIdsByStatusAndPassedDeadline(CANCEL_FROM_MENTEE_FLOW, 월요일_4주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[3].id)
            sut.findIdsByStatusAndPassedDeadline(AUTO_CANCEL_FROM_MENTEE_FLOW, now) shouldBe emptyList()

            sut.findIdsByStatusAndPassedDeadline(MENTOR_SUGGEST, now) shouldBe emptyList()
            sut.findIdsByStatusAndPassedDeadline(MENTEE_REJECT, now) shouldBe emptyList()
            sut.findIdsByStatusAndPassedDeadline(MENTEE_PENDING, 수요일_2주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[7].id)
            sut.findIdsByStatusAndPassedDeadline(MENTEE_PENDING, 수요일_3주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[7].id, coffeeChats[8].id)
            sut.findIdsByStatusAndPassedDeadline(MENTOR_FINALLY_CANCEL, now) shouldBe emptyList()
            sut.findIdsByStatusAndPassedDeadline(MENTOR_FINALLY_APPROVE, 수요일_4주차_20_00_시작.start) shouldContainExactlyInAnyOrder listOf(coffeeChats[9].id)
            sut.findIdsByStatusAndPassedDeadline(MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE, now) shouldBe emptyList()
            sut.findIdsByStatusAndPassedDeadline(CANCEL_FROM_MENTOR_FLOW, now) shouldBe emptyList()
            sut.findIdsByStatusAndPassedDeadline(AUTO_CANCEL_FROM_MENTOR_FLOW, now) shouldBe emptyList()
        }
    }

    @Test
    fun `특정 커피챗들에 대해서 Status를 일괄 업데이트한다 - updateStatusInBatch`() {
        // given
        val mentees: List<Mentee> = memberRepository.saveAll(
            listOf(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
                MENTEE_3.toDomain(),
                MENTEE_4.toDomain(),
                MENTEE_5.toDomain(),
            ),
        )
        val mentors: List<Mentor> = memberRepository.saveAll(
            listOf(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain(),
            ),
        )
        val coffeeChats: List<CoffeeChat> = sut.saveAll(
            listOf(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                MenteeFlow.apply(월요일_3주차_20_00_시작, mentees[2], mentors[0]),
                MenteeFlow.applyAndApprove(월요일_4주차_20_00_시작, mentees[3], mentors[0]),
                MenteeFlow.applyAndApprove(수요일_1주차_20_00_시작, mentees[4], mentors[0]),
                MentorFlow.suggestAndPending(수요일_2주차_20_00_시작, mentors[1], mentees[0]),
                MentorFlow.suggestAndPending(수요일_3주차_20_00_시작, mentors[1], mentees[1]),
                MentorFlow.suggestAndPending(수요일_4주차_20_00_시작, mentors[1], mentees[2]),
                MentorFlow.suggestAndFinallyApprove(금요일_1주차_20_00_시작, mentors[1], mentees[3]),
                MentorFlow.suggestAndFinallyApprove(금요일_2주차_20_00_시작, mentors[1], mentees[4]),
            ),
        )

        // when
        sut.updateStatusInBatch(
            listOf(coffeeChats[0].id, coffeeChats[1].id, coffeeChats[2].id),
            AUTO_CANCEL_FROM_MENTEE_FLOW,
        )
        sut.updateStatusInBatch(
            listOf(coffeeChats[3].id, coffeeChats[4].id),
            MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
        )
        sut.updateStatusInBatch(
            listOf(coffeeChats[5].id, coffeeChats[6].id, coffeeChats[7].id),
            AUTO_CANCEL_FROM_MENTOR_FLOW,
        )
        sut.updateStatusInBatch(
            listOf(coffeeChats[8].id, coffeeChats[9].id),
            MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE,
        )

        // then
        assertSoftly {
            sut.findByIdOrNull(coffeeChats[0].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
            sut.findByIdOrNull(coffeeChats[1].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
            sut.findByIdOrNull(coffeeChats[2].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
            sut.findByIdOrNull(coffeeChats[3].id)!!.status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            sut.findByIdOrNull(coffeeChats[4].id)!!.status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            sut.findByIdOrNull(coffeeChats[5].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
            sut.findByIdOrNull(coffeeChats[6].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
            sut.findByIdOrNull(coffeeChats[7].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
            sut.findByIdOrNull(coffeeChats[8].id)!!.status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
            sut.findByIdOrNull(coffeeChats[9].id)!!.status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
        }
    }

    @Test
    fun `멘토-멘티 자신들과 연관된 커피챗을 가져온다 - findByIdAndMentorId & findByIdAndMenteeId`() {
        // given
        val mentees: List<Mentee> = memberRepository.saveAll(
            listOf(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
            ),
        )
        val mentors: List<Mentor> = memberRepository.saveAll(
            listOf(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain(),
            ),
        )
        val coffeeChats: List<CoffeeChat> = sut.saveAll(
            listOf(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[1], mentors[1]),
            ),
        )

        // when - then
        assertSoftly {
            sut.findByIdAndMentorId(coffeeChats[0].id, mentors[0].id) shouldBe coffeeChats[0]
            sut.findByIdAndMentorId(coffeeChats[0].id, mentors[1].id) shouldBe null
            sut.findByIdAndMentorId(coffeeChats[1].id, mentors[0].id) shouldBe null
            sut.findByIdAndMentorId(coffeeChats[1].id, mentors[1].id) shouldBe coffeeChats[1]

            sut.findByIdAndMenteeId(coffeeChats[0].id, mentees[0].id) shouldBe coffeeChats[0]
            sut.findByIdAndMenteeId(coffeeChats[0].id, mentees[1].id) shouldBe null
            sut.findByIdAndMenteeId(coffeeChats[1].id, mentees[0].id) shouldBe null
            sut.findByIdAndMenteeId(coffeeChats[1].id, mentees[1].id) shouldBe coffeeChats[1]
        }
    }
}
