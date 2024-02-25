package com.koddy.server.notification.application.usecase

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.notification.application.usecase.command.ReadSingleNotificationCommand
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST
import com.koddy.server.notification.domain.repository.NotificationRepository
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
    val mentee: Mentee = MENTEE_1.toDomain().apply(1L)
    val coffeeChat: CoffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L)

    describe("ReadNotificationUseCase's readSingle") {
        val notification = Notification.create(mentor, coffeeChat, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST).apply(1L)
        every { notificationRepository.getByIdAndTargetId(notification.id, mentor.id) } returns notification

        context("아직 읽지 않은 자신의 Notification에 접근하면") {
            val command = ReadSingleNotificationCommand(mentor.id, notification.id)

            it("읽음 처리를 진행한다") {
                sut.readSingle(command)

                notification.isRead shouldBe true
            }
        }
    }
})
