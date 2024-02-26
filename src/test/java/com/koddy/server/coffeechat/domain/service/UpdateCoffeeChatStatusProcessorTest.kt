package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.IntegrateTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@IntegrateTestKt
@DisplayName("CoffeeChat -> UpdateCoffeeChatStatusProcessor 테스트 [IntegrateTest]")
internal class UpdateCoffeeChatStatusProcessorTest(
    private val sut: UpdateCoffeeChatStatusProcessor,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    private var mentees = mutableListOf<Mentee>()
    private var mentors = mutableListOf<Mentor>()

    @BeforeEach
    fun setUp() {
        mentees = memberRepository.saveAll(
            listOf(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
            ),
        )
        mentors = memberRepository.saveAll(
            listOf(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain(),
            ),
        )
    }

    @Test
    fun `대기 상태(Waiting) 커피챗들의 진행 예정 시간이 standard 이전이면 자동 취소 상태(AUTO_CANCEL_XXX)로 변경한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                MentorFlow.suggestAndPending(수요일_2주차_20_00_시작, mentors[1], mentees[0]),
                MentorFlow.suggestAndPending(수요일_3주차_20_00_시작, mentors[1], mentees[1]),
            ),
        )

        // when - then
        sut.updateWaitingToAutoCancel(수요일_2주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe CoffeeChatStatus.AUTO_CANCEL_FROM_MENTOR_FLOW
            coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe CoffeeChatStatus.MENTEE_PENDING
        }

        sut.updateWaitingToAutoCancel(수요일_3주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW
            coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe CoffeeChatStatus.AUTO_CANCEL_FROM_MENTOR_FLOW
            coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe CoffeeChatStatus.AUTO_CANCEL_FROM_MENTOR_FLOW
        }
    }

    @Test
    fun `예정 상태(Scheduled) 커피챗들의 진행 시간이 standard 이전이면 완료 상태(XXX_COFFEE_CHAT_COMPLETE)로 변경한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.applyAndApprove(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.applyAndApprove(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                MentorFlow.suggestAndFinallyApprove(수요일_2주차_20_00_시작, mentors[1], mentees[0]),
                MentorFlow.suggestAndFinallyApprove(수요일_3주차_20_00_시작, mentors[1], mentees[1]),
            ),
        )

        // when - then
        sut.updateScheduledToComplete(수요일_2주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe CoffeeChatStatus.MENTOR_FINALLY_APPROVE
        }

        sut.updateScheduledToComplete(수요일_3주차_20_00_시작.start)
        assertSoftly {
            coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
            coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
        }
    }
}
