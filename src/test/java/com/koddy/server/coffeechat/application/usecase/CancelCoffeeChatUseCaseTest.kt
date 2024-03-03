package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand
import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.event.MentorNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFlow
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
@DisplayName("CoffeeChat -> CancelCoffeeChatUseCase 테스트")
internal class CancelCoffeeChatUseCaseTest : DescribeSpec({
    val coffeeChatReader = mockk<CoffeeChatReader>()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val coffeeChatNotificationEventPublisher = CoffeeChatNotificationEventPublisher(eventPublisher)
    val sut = CancelCoffeeChatUseCase(
        coffeeChatReader,
        coffeeChatNotificationEventPublisher,
    )

    val mentor: Mentor = MENTOR_1.toDomain().apply(1L)
    val mentee: Mentee = MENTEE_1.toDomain().apply(2L)

    describe("CancelCoffeeChatUseCase's invoke (멘토가 취소)") {
        val authenticated = Authenticated(mentor.id, mentor.authority)

        context("MenteeFlow로 진행되는 커피챗을 멘토가 취소하면") {
            val coffeeChat: CoffeeChat = MenteeFlow.applyAndApprove(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)
            val command = CancelCoffeeChatCommand(
                authenticated = authenticated,
                coffeeChatId = coffeeChat.id,
                cancelReason = "취소..",
            )
            every { coffeeChatReader.getByMentor(command.coffeeChatId, authenticated.id) } returns coffeeChat

            val slotEvent = slot<MenteeNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 취소되고 멘티에게 알림이 전송된다") {
                sut.invoke(command)

                verify(exactly = 1) { coffeeChatReader.getByMentor(command.coffeeChatId, authenticated.id) }
                slotEvent.captured shouldBe MenteeNotification.MentorCanceledFromMenteeFlowEvent(
                    menteeId = coffeeChat.menteeId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe CANCEL_FROM_MENTEE_FLOW
                    cancelBy shouldBe mentor.id
                    reason.cancelReason shouldBe command.cancelReason

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    cancelBy shouldBe mentor.id
                    reason.applyReason shouldNotBe null
                    reason.suggestReason shouldBe null
                    reason.rejectReason shouldBe null
                    question shouldNotBe null
                    reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                    reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                    strategy!!.type shouldNotBe null
                    strategy!!.value shouldNotBe null
                }
            }
        }

        context("MentorFlow로 진행되는 커피챗을 멘토가 취소하면") {
            val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(id = 1L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee)
            val command = CancelCoffeeChatCommand(
                authenticated = authenticated,
                coffeeChatId = coffeeChat.id,
                cancelReason = "취소..",
            )
            every { coffeeChatReader.getByMentor(command.coffeeChatId, authenticated.id) } returns coffeeChat

            val slotEvent = slot<MenteeNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 취소되고 멘티에게 알림이 전송된다") {
                sut.invoke(command)

                verify(exactly = 1) { coffeeChatReader.getByMentor(command.coffeeChatId, authenticated.id) }
                slotEvent.captured shouldBe MenteeNotification.MentorCanceledFromMentorFlowEvent(
                    menteeId = coffeeChat.menteeId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe CANCEL_FROM_MENTOR_FLOW
                    reason.cancelReason shouldBe command.cancelReason
                    cancelBy shouldBe mentor.id

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
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

    describe("CancelCoffeeChatUseCase's invoke (멘티가 취소)") {
        val authenticated = Authenticated(mentee.id, mentee.authority)

        context("MenteeFlow로 진행되는 커피챗을 멘티가 취소하면") {
            val coffeeChat: CoffeeChat = MenteeFlow.applyAndApprove(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)
            val command = CancelCoffeeChatCommand(
                authenticated = authenticated,
                coffeeChatId = coffeeChat.id,
                cancelReason = "취소..",
            )
            every { coffeeChatReader.getByMentee(command.coffeeChatId, authenticated.id) } returns coffeeChat

            val slotEvent = slot<MentorNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 취소되고 멘토에게 알림이 전송된다") {
                sut.invoke(command)

                verify(exactly = 1) { coffeeChatReader.getByMentee(command.coffeeChatId, authenticated.id) }
                slotEvent.captured shouldBe MentorNotification.MenteeCanceledFromMenteeFlowEvent(
                    mentorId = coffeeChat.mentorId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe CANCEL_FROM_MENTEE_FLOW
                    cancelBy shouldBe mentee.id
                    reason.cancelReason shouldBe command.cancelReason

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    reason.applyReason shouldNotBe null
                    reason.suggestReason shouldBe null
                    reason.rejectReason shouldBe null
                    question shouldNotBe null
                    reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                    reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                    strategy!!.type shouldNotBe null
                    strategy!!.value shouldNotBe null
                }
            }
        }

        context("MentorFlow로 진행되는 커피챗을 멘티가 취소하면") {
            val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(id = 1L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee)
            val command = CancelCoffeeChatCommand(
                authenticated = authenticated,
                coffeeChatId = coffeeChat.id,
                cancelReason = "취소..",
            )
            every { coffeeChatReader.getByMentee(command.coffeeChatId, authenticated.id) } returns coffeeChat

            val slotEvent = slot<MentorNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 취소되고 멘토에게 알림이 전송된다") {
                sut.invoke(command)

                verify(exactly = 1) { coffeeChatReader.getByMentee(command.coffeeChatId, authenticated.id) }
                slotEvent.captured shouldBe MentorNotification.MenteeCanceledFromMentorFlowEvent(
                    mentorId = coffeeChat.mentorId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe CANCEL_FROM_MENTOR_FLOW
                    cancelBy shouldBe mentee.id
                    reason.cancelReason shouldBe command.cancelReason

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
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
})
