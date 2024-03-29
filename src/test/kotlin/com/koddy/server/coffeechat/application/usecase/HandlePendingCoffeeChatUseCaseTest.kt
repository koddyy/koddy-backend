package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.FinallyApprovePendingCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.FinallyCancelPendingCoffeeChatCommand
import com.koddy.server.coffeechat.domain.event.BothNotification
import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.common.mock.fake.FakeEncryptor
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher

@UnitTestKt
@DisplayName("CoffeeChat -> HandlePendingCoffeeChatUseCase 테스트")
internal class HandlePendingCoffeeChatUseCaseTest : DescribeSpec({
    val coffeeChatReader = mockk<CoffeeChatReader>()
    val encryptor = FakeEncryptor()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val coffeeChatNotificationEventPublisher = CoffeeChatNotificationEventPublisher(eventPublisher)
    val sut = HandlePendingCoffeeChatUseCase(
        coffeeChatReader,
        encryptor,
        coffeeChatNotificationEventPublisher,
    )

    val mentor: Mentor = mentorFixture(id = 1L).toDomain()
    val mentee: Mentee = menteeFixture(id = 2L).toDomain()

    describe("HandlePendingCoffeeChatUseCase's finallyCancel") {
        val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(id = 1L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee)

        context("멘티가 1차 수락한 커피챗에 대해서 멘토가 최종 취소하면") {
            val command = FinallyCancelPendingCoffeeChatCommand(
                mentorId = mentor.id,
                coffeeChatId = coffeeChat.id,
                cancelReason = "취소..",
            )
            every { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) } returns coffeeChat

            val slotEvent = slot<MenteeNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗은 최종 취소 상태가 되고 멘티에게 알림이 전송된다") {
                sut.finallyCancel(command)

                verify(exactly = 1) { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) }
                slotEvent.captured shouldBe MenteeNotification.MentorFinallyCanceledFromMentorFlowEvent(
                    menteeId = coffeeChat.menteeId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe MENTOR_FINALLY_CANCEL
                    reason.cancelReason shouldBe command.cancelReason

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    cancelBy shouldBe null
                    reason.applyReason shouldBe null
                    reason.suggestReason shouldNotBe null
                    reason.rejectReason shouldBe null
                    question shouldNotBe null
                    reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                    reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                    strategy shouldBe null
                }
            }
        }
    }

    describe("HandlePendingCoffeeChatUseCase's finallyApprove") {
        val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(id = 1L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee)

        context("멘티가 1차 수락한 커피챗에 대해서 멘토가 최종 수락하면") {
            val command = FinallyApprovePendingCoffeeChatCommand(
                mentorId = mentor.id,
                coffeeChatId = coffeeChat.id,
                type = Strategy.Type.KAKAO_ID,
                value = "sjiwon",
            )
            every { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) } returns coffeeChat

            val slotEvent = slot<BothNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 예정되고 멘토 & 멘티에게 알림이 전송된다") {
                sut.finallyApprove(command)

                verify(exactly = 1) { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) }
                slotEvent.captured shouldBe BothNotification.FinallyApprovedFromMentorFlowEvent(
                    menteeId = coffeeChat.menteeId,
                    mentorId = coffeeChat.mentorId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe MENTOR_FINALLY_APPROVE
                    strategy!!.type shouldBe command.type
                    strategy!!.value shouldNotBe command.value
                    encryptor.decrypt(strategy!!.value) shouldBe command.value

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    cancelBy shouldBe null
                    reason.applyReason shouldBe null
                    reason.suggestReason shouldNotBe null
                    reason.cancelReason shouldBe null
                    reason.rejectReason shouldBe null
                    question shouldNotBe null
                    reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                    reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                }
            }
        }
    }
})
