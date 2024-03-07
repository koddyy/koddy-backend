package com.koddy.server.notification.application.usecase

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.common.fixture.NotificationFixture.멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.notification.application.usecase.command.ReadSingleNotificationCommand
import com.koddy.server.notification.domain.repository.NotificationRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@UnitTestKt
@DisplayName("Notification -> ReadNotificationUseCase 테스트")
internal class ReadNotificationUseCaseTest : DescribeSpec({
    val notificationRepository = mockk<NotificationRepository>()
    val sut = ReadNotificationUseCase(notificationRepository)

    val mentor: Mentor = mentorFixture(id = 1L).toDomain()
    val mentee: Mentee = menteeFixture(id = 2L).toDomain()
    val coffeeChat1: CoffeeChat = MentorFlow.suggest(id = 1L, mentor = mentor, mentee = mentee)
    val coffeeChat2: CoffeeChat = MenteeFlow.apply(id = 2L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

    describe("ReadNotificationUseCase's readSingle") {
        val notification1 = 멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(id = 1, target = mentee, coffeeChat = coffeeChat1)
        val notification2 = 멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW.toDomain(id = 2, target = mentee, coffeeChat = coffeeChat2)
        every { notificationRepository.getByIdAndTargetId(notification1.id, mentee.id) } returns notification1
        every { notificationRepository.getByIdAndTargetId(notification2.id, mentor.id) } returns notification2

        context("아직 읽지 않은 자신의 Notification에 접근하면") {
            val command1 = ReadSingleNotificationCommand(mentee.id, notification1.id)
            val command2 = ReadSingleNotificationCommand(mentor.id, notification2.id)

            it("읽음 처리를 진행한다") {
                sut.readSingle(command1)
                assertSoftly {
                    notification1.isRead shouldBe true
                    notification2.isRead shouldBe false
                }

                sut.readSingle(command2)
                assertSoftly {
                    notification1.isRead shouldBe true
                    notification2.isRead shouldBe true
                }
            }
        }
    }
})
