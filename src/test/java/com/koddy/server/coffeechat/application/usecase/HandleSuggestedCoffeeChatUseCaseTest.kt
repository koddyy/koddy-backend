package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand
import com.koddy.server.coffeechat.domain.event.MentorNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture
import com.koddy.server.common.fixture.MentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MentorRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.Called
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher

@UnitTestKt
@DisplayName("CoffeeChat -> HandleSuggestedCoffeeChatUseCase 테스트")
internal class HandleSuggestedCoffeeChatUseCaseTest : DescribeSpec({
    val coffeeChatReader = mockk<CoffeeChatReader>()
    val mentorRepository = mockk<MentorRepository>()
    val reservationAvailabilityChecker = mockk<ReservationAvailabilityChecker>()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val coffeeChatNotificationEventPublisher = CoffeeChatNotificationEventPublisher(eventPublisher)
    val sut = HandleSuggestedCoffeeChatUseCase(
        coffeeChatReader,
        mentorRepository,
        reservationAvailabilityChecker,
        coffeeChatNotificationEventPublisher,
    )

    val mentor: Mentor = MentorFixture.MENTOR_1.toDomain().apply(1L)
    val mentee: Mentee = MenteeFixture.MENTEE_1.toDomain().apply(2L)

    describe("HandleSuggestedCoffeeChatUseCase's reject") {
        val coffeeChat: CoffeeChat = MentorFlow.suggest(id = 1L, mentor = mentor, mentee = mentee)

        context("멘토가 제안한 커피챗에 대해서 멘티가 거절하면") {
            val command = RejectSuggestedCoffeeChatCommand(
                menteeId = mentee.id,
                coffeeChatId = coffeeChat.id,
                rejectReason = "거절..",
            )
            every { coffeeChatReader.getByMentee(command.coffeeChatId, command.menteeId) } returns coffeeChat

            val slotEvent = slot<MentorNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 거절되고 멘토에게 알림이 전송된다") {
                sut.reject(command)

                verify(exactly = 1) { coffeeChatReader.getByMentee(command.coffeeChatId, command.menteeId) }
                verify { reservationAvailabilityChecker wasNot Called }
                slotEvent.captured shouldBe MentorNotification.MenteeRejectedFromMentorFlowEvent(
                    mentorId = coffeeChat.mentorId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe MENTEE_REJECT
                    reason.rejectReason shouldBe command.rejectReason

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    cancelBy shouldBe null
                    reason.applyReason shouldBe null
                    reason.suggestReason shouldNotBe null
                    reason.cancelReason shouldBe null
                    question shouldBe null
                    reservation shouldBe null
                    strategy shouldBe null
                }
            }
        }
    }

    describe("HandleSuggestedCoffeeChatUseCase's pending") {
        val coffeeChat: CoffeeChat = MentorFlow.suggest(id = 1L, mentor = mentor, mentee = mentee)

        context("멘토가 제안한 커피챗에 대해서 멘티가 1차 수락하면") {
            val command = PendingSuggestedCoffeeChatCommand(
                mentee.id,
                coffeeChat.id,
                "질문..",
                Reservation(
                    start = 월요일_1주차_20_00_시작.start,
                    end = 월요일_1주차_20_00_시작.end,
                ),
            )
            every { coffeeChatReader.getByMentee(command.coffeeChatId, command.menteeId) } returns coffeeChat
            every { mentorRepository.getByIdWithSchedules(coffeeChat.mentorId) } returns mentor
            justRun { reservationAvailabilityChecker.check(mentor, command.reservation) }

            val slotEvent = slot<MentorNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 1차 수락 상태가 되고 멘토에게 알림이 전송된다") {
                sut.pending(command)

                verify(exactly = 1) {
                    coffeeChatReader.getByMentee(command.coffeeChatId, command.menteeId)
                    mentorRepository.getByIdWithSchedules(coffeeChat.mentorId)
                    reservationAvailabilityChecker.check(mentor, command.reservation)
                }
                slotEvent.captured shouldBe MentorNotification.MenteePendedFromMentorFlowEvent(
                    mentorId = coffeeChat.mentorId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe MENTEE_PENDING
                    question shouldBe command.question
                    reservation!!.start shouldBe command.reservation.start
                    reservation!!.end shouldBe command.reservation.end

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    cancelBy shouldBe null
                    reason.applyReason shouldBe null
                    reason.suggestReason shouldNotBe null
                    reason.cancelReason shouldBe null
                    reason.rejectReason shouldBe null
                    strategy shouldBe null
                }
            }
        }
    }
})
