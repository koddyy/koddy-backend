package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.coffeechat.application.usecase.command.ApproveAppliedCoffeeChatCommand
import com.koddy.server.coffeechat.application.usecase.command.RejectAppliedCoffeeChatCommand
import com.koddy.server.coffeechat.domain.event.BothNotification
import com.koddy.server.coffeechat.domain.event.MenteeNotification
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT
import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
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
@DisplayName("CoffeeChat -> HandleAppliedCoffeeChatUseCase 테스트")
internal class HandleAppliedCoffeeChatUseCaseTest : DescribeSpec({
    val coffeeChatReader = mockk<CoffeeChatReader>()
    val encryptor = FakeEncryptor()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val coffeeChatNotificationEventPublisher = CoffeeChatNotificationEventPublisher(eventPublisher)
    val sut = HandleAppliedCoffeeChatUseCase(
        coffeeChatReader,
        encryptor,
        coffeeChatNotificationEventPublisher,
    )

    val mentor: Mentor = mentorFixture(id = 1L).toDomain()
    val mentee: Mentee = menteeFixture(id = 2L).toDomain()

    describe("HandleAppliedCoffeeChatUseCase's reject") {
        val coffeeChat: CoffeeChat = MenteeFlow.apply(1L, 월요일_1주차_20_00_시작, mentee, mentor)

        context("멘티가 신청한 커피챗에 대해서 멘토가 거절하면") {
            val command = RejectAppliedCoffeeChatCommand(
                mentorId = mentor.id,
                coffeeChatId = coffeeChat.id,
                rejectReason = "거절..",
            )
            every { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) } returns coffeeChat

            val slotEvent = slot<MenteeNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 거절되고 멘티에게 알림이 전송된다") {
                sut.reject(command)

                verify(exactly = 1) { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) }
                slotEvent.captured shouldBe MenteeNotification.MentorRejectedFromMenteeFlowEvent(
                    menteeId = coffeeChat.menteeId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe MENTOR_REJECT
                    reason.rejectReason shouldBe command.rejectReason

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    cancelBy shouldBe null
                    reason.applyReason shouldNotBe null
                    reason.suggestReason shouldBe null
                    reason.cancelReason shouldBe null
                    question shouldBe null
                    reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                    reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                    strategy shouldBe null
                }
            }
        }
    }

    describe("HandleAppliedCoffeeChatUseCase's approve") {
        val coffeeChat: CoffeeChat = MenteeFlow.apply(1L, 월요일_1주차_20_00_시작, mentee, mentor)

        context("멘티가 신청한 커피챗에 대해서 멘토가 수락하면") {
            val command = ApproveAppliedCoffeeChatCommand(
                mentorId = mentor.id,
                coffeeChatId = coffeeChat.id,
                question = "멘티에게 궁금한점..",
                type = Strategy.Type.KAKAO_ID,
                value = "sjiwon",
            )
            every { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) } returns coffeeChat

            val slotEvent = slot<BothNotification>()
            justRun { eventPublisher.publishEvent(capture(slotEvent)) }

            it("커피챗이 예정되고 멘토 & 멘티에게 알림이 전송된다") {
                sut.approve(command)

                verify(exactly = 1) { coffeeChatReader.getByMentor(command.coffeeChatId, command.mentorId) }
                slotEvent.captured shouldBe BothNotification.ApprovedFromMenteeFlowEvent(
                    menteeId = coffeeChat.menteeId,
                    mentorId = coffeeChat.mentorId,
                    coffeeChatId = coffeeChat.id,
                )
                assertSoftly(coffeeChat) {
                    // Effected
                    status shouldBe MENTOR_APPROVE
                    question shouldBe command.question
                    strategy!!.type shouldBe command.type
                    strategy!!.value shouldNotBe command.value
                    encryptor.decrypt(strategy!!.value) shouldBe command.value

                    // Not Effected
                    mentorId shouldBe mentor.id
                    menteeId shouldBe mentee.id
                    cancelBy shouldBe null
                    reason.applyReason shouldNotBe null
                    reason.suggestReason shouldBe null
                    reason.cancelReason shouldBe null
                    reason.rejectReason shouldBe null
                    reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                    reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                }
            }
        }
    }
})
