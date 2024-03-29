package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.IntegrateTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
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
import org.springframework.data.repository.findByIdOrNull

@IntegrateTestKt
@DisplayName("CoffeeChat -> UpdateCoffeeChatStatusProcessor 테스트 [IntegrateTest]")
internal class UpdateCoffeeChatStatusProcessorTest(
    private val sut: UpdateCoffeeChatStatusProcessor,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    private lateinit var mentees: List<Mentee>
    private lateinit var mentors: List<Mentor>

    @BeforeEach
    fun setUp() {
        mentees = memberRepository.saveAll(
            listOf(
                menteeFixture(sequence = 1).toDomain(),
                menteeFixture(sequence = 2).toDomain(),
            ),
        )
        mentors = memberRepository.saveAll(
            listOf(
                mentorFixture(sequence = 1).toDomain(),
                mentorFixture(sequence = 2).toDomain(),
            ),
        )
    }

    @Test
    fun `대기 상태(Waiting) 커피챗들의 진행 예정 시간이 standard 이전이면 자동 취소 상태(AUTO_CANCEL_XXX)로 변경한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.apply(fixture = 월요일_1주차_20_00_시작, mentee = mentees[0], mentor = mentors[0]),
                MenteeFlow.apply(fixture = 월요일_2주차_20_00_시작, mentee = mentees[1], mentor = mentors[0]),
                MentorFlow.suggestAndPending(fixture = 수요일_2주차_20_00_시작, mentor = mentors[1], mentee = mentees[0]),
                MentorFlow.suggestAndPending(fixture = 수요일_3주차_20_00_시작, mentor = mentors[1], mentee = mentees[1]),
            ),
        )

        // when - then
        sut.updateWaitingToAutoCancel(수요일_2주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.findByIdOrNull(coffeeChats[0].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.findByIdOrNull(coffeeChats[1].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.findByIdOrNull(coffeeChats[2].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
            coffeeChatRepository.findByIdOrNull(coffeeChats[3].id)!!.status shouldBe MENTEE_PENDING
        }

        sut.updateWaitingToAutoCancel(수요일_3주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.findByIdOrNull(coffeeChats[0].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.findByIdOrNull(coffeeChats[1].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.findByIdOrNull(coffeeChats[2].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
            coffeeChatRepository.findByIdOrNull(coffeeChats[3].id)!!.status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
        }
    }

    @Test
    fun `예정 상태(Scheduled) 커피챗들의 진행 시간이 standard 이전이면 완료 상태(XXX_COFFEE_CHAT_COMPLETE)로 변경한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.applyAndApprove(fixture = 월요일_1주차_20_00_시작, mentee = mentees[0], mentor = mentors[0]),
                MenteeFlow.applyAndApprove(fixture = 월요일_2주차_20_00_시작, mentee = mentees[1], mentor = mentors[0]),
                MentorFlow.suggestAndFinallyApprove(fixture = 수요일_2주차_20_00_시작, mentor = mentors[1], mentee = mentees[0]),
                MentorFlow.suggestAndFinallyApprove(fixture = 수요일_3주차_20_00_시작, mentor = mentors[1], mentee = mentees[1]),
            ),
        )

        // when - then
        sut.updateScheduledToComplete(수요일_2주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.findByIdOrNull(coffeeChats[0].id)!!.status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.findByIdOrNull(coffeeChats[1].id)!!.status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.findByIdOrNull(coffeeChats[2].id)!!.status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.findByIdOrNull(coffeeChats[3].id)!!.status shouldBe MENTOR_FINALLY_APPROVE
        }

        sut.updateScheduledToComplete(수요일_3주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.findByIdOrNull(coffeeChats[0].id)!!.status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.findByIdOrNull(coffeeChats[1].id)!!.status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.findByIdOrNull(coffeeChats[2].id)!!.status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.findByIdOrNull(coffeeChats[3].id)!!.status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
        }
    }
}
