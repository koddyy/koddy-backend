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
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_3
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

@IntegrateTestKt
@DisplayName("CoffeeChat -> UpdateCoffeeChatStatusProcessor 테스트 [IntegrateTest]")
internal class UpdateCoffeeChatStatusProcessorTest @Autowired constructor(
    private val sut: UpdateCoffeeChatStatusProcessor,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) : DescribeSpec({
    val mentees: List<Mentee> = memberRepository.saveAll(
        listOf(
            MENTEE_1.toDomain(),
            MENTEE_2.toDomain(),
            MENTEE_3.toDomain(),
        ),
    )
    val mentors: List<Mentor> = memberRepository.saveAll(
        listOf(
            MENTOR_1.toDomain(),
            MENTOR_2.toDomain(),
        ),
    )

    describe("UpdateCoffeeChatStatusProcessorTest's updateWaitingToAutoCancel") {
        context("대기(Waiting) 상태 커피챗들의 진행 예정 시간이 standard 이전이면") {
            val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
                listOf(
                    MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                    MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                    MentorFlow.suggestAndPending(수요일_2주차_20_00_시작, mentors[1], mentees[0]),
                    MentorFlow.suggestAndPending(수요일_3주차_20_00_시작, mentors[1], mentees[1]),
                ),
            )

            it("자동 취소(AUTO_CANCEL_...)로 상태를 변경한다") {
                sut.updateWaitingToAutoCancel(수요일_2주차_20_00_시작.start)
                assertSoftly {
                    coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
                    coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
                    coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
                    coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe MENTEE_PENDING
                }

                sut.updateWaitingToAutoCancel(수요일_3주차_20_00_시작.start)
                assertSoftly {
                    coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
                    coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe AUTO_CANCEL_FROM_MENTEE_FLOW
                    coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
                    coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe AUTO_CANCEL_FROM_MENTOR_FLOW
                }
            }
        }
    }

    describe("UpdateCoffeeChatStatusProcessorTest's updateScheduledToComplete") {
        context("예정(Scheduled) 상태 커피챗들의 진행 예정 시간이 standard 이전이면") {
            val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
                listOf(
                    MenteeFlow.applyAndApprove(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                    MenteeFlow.applyAndApprove(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                    MentorFlow.suggestAndFinallyApprove(수요일_2주차_20_00_시작, mentors[1], mentees[0]),
                    MentorFlow.suggestAndFinallyApprove(수요일_3주차_20_00_시작, mentors[1], mentees[1]),
                ),
            )

            it("완료(...COFFEE_CHAT_COMPLETE)로 상태를 변경한다") {
                sut.updateScheduledToComplete(수요일_2주차_20_00_시작.start)
                assertSoftly {
                    coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
                    coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
                    coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                    coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe MENTOR_FINALLY_APPROVE
                }

                sut.updateScheduledToComplete(수요일_3주차_20_00_시작.start)
                assertSoftly {
                    coffeeChatRepository.getById(coffeeChats[0].id).status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
                    coffeeChatRepository.getById(coffeeChats[1].id).status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE
                    coffeeChatRepository.getById(coffeeChats[2].id).status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                    coffeeChatRepository.getById(coffeeChats[3].id).status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                }
            }
        }
    }
})
