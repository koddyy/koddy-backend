package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatByApplyCommand
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatBySuggestCommand
import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.event.MentorNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatWriter
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher

@UnitTestKt
@DisplayName("CoffeeChat -> CreateCoffeeChatUseCase 테스트")
internal class CreateCoffeeChatUseCaseTest : DescribeSpec({
    val memberReader = mockk<MemberReader>()
    val reservationAvailabilityChecker = mockk<ReservationAvailabilityChecker>()
    val coffeeChatWriter = mockk<CoffeeChatWriter>()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val coffeeChatNotificationEventPublisher = CoffeeChatNotificationEventPublisher(eventPublisher)
    val sut = CreateCoffeeChatUseCase(
        memberReader,
        reservationAvailabilityChecker,
        coffeeChatWriter,
        coffeeChatNotificationEventPublisher,
    )

    val mentor: Mentor = mentorFixture(id = 1L).toDomain()
    val mentee: Mentee = menteeFixture(id = 2L).toDomain()

    describe("CreateCoffeeChatUseCase's createByApply") {
        val command = CreateCoffeeChatByApplyCommand(
            menteeId = mentee.id,
            mentorId = mentor.id,
            applyReason = "신청..",
            reservation = Reservation(
                start = 월요일_1주차_20_00_시작.start,
                end = 월요일_1주차_20_00_시작.end,
            ),
        )
        every { memberReader.getMentee(command.menteeId) } returns mentee
        every { memberReader.getMentor(command.mentorId) } returns mentor
        justRun { reservationAvailabilityChecker.check(mentor, command.reservation) }

        context("멘티가 멘토에게 커피챗을 신청하면") {
            val coffeeChat = CoffeeChat.applyFixture(
                id = 1L,
                mentee = mentee,
                mentor = mentor,
                applyReason = command.applyReason,
                reservation = command.reservation,
            )
            every { coffeeChatWriter.save(any(CoffeeChat::class)) } returns coffeeChat

            val slotEvent = slot<MentorNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 생성되고 멘토에게 알림이 전송된다") {
                val coffeeChatId: Long = sut.createByApply(command)

                verify(exactly = 1) {
                    memberReader.getMentee(command.menteeId)
                    memberReader.getMentor(command.mentorId)
                    reservationAvailabilityChecker.check(mentor, command.reservation)
                    coffeeChatWriter.save(any(CoffeeChat::class))
                }
                slotEvent.captured shouldBe MentorNotification.MenteeAppliedFromMenteeFlowEvent(
                    mentorId = coffeeChat.mentorId,
                    coffeeChatId = coffeeChat.id,
                )
                coffeeChatId shouldBe coffeeChat.id
            }
        }
    }

    describe("CreateCoffeeChatUseCase's createBySuggest") {
        val command = CreateCoffeeChatBySuggestCommand(
            mentorId = mentor.id,
            menteeId = mentee.id,
            suggestReason = "제안..",
        )
        every { memberReader.getMentor(command.mentorId) } returns mentor
        every { memberReader.getMentee(command.menteeId) } returns mentee

        context("멘토가 멘티에게 커피챗을 제안하면") {
            val coffeeChat = CoffeeChat.suggestFixture(
                id = 1L,
                mentor = mentor,
                mentee = mentee,
                suggestReason = command.suggestReason,
            )
            every { coffeeChatWriter.save(any(CoffeeChat::class)) } returns coffeeChat

            val slotEvent = slot<MenteeNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 생성되고 멘티에게 알림이 전송된다") {
                val coffeeChatId: Long = sut.createBySuggest(command)

                verify(exactly = 1) {
                    memberReader.getMentor(command.mentorId)
                    memberReader.getMentee(command.menteeId)
                    coffeeChatWriter.save(any(CoffeeChat::class))
                }
                verify { reservationAvailabilityChecker wasNot Called }
                slotEvent.captured shouldBe MenteeNotification.MentorSuggestedFromMentorFlowEvent(
                    menteeId = coffeeChat.menteeId,
                    coffeeChatId = coffeeChat.id,
                )
                coffeeChatId shouldBe coffeeChat.id
            }
        }
    }
})
