package com.koddy.server.notification.application.usecase

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.notification.application.usecase.command.ReadSingleNotificationCommand
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY
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

    val mentor: Mentor = MENTOR_1.toDomain().apply(1L)
    val mentee: Mentee = MENTEE_1.toDomain().apply(2L)
    val coffeeChat1: CoffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L)
    val coffeeChat2: CoffeeChat = MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor).apply(2L)

    describe("ReadNotificationUseCase's readSingle") {
        val notification1 = Notification.create(mentee, coffeeChat1, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST).apply(1L)
        val notification2 = Notification.create(mentor, coffeeChat2, MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY).apply(2L)
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
