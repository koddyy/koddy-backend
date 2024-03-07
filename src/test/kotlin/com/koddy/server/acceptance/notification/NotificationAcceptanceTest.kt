package com.koddy.server.acceptance.notification

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다
import com.koddy.server.acceptance.notification.NotificationAcceptanceStep.단건_알림을_읽음_처리한다
import com.koddy.server.acceptance.notification.NotificationAcceptanceStep.알림을_조회한다
import com.koddy.server.acceptance.notification.NotificationAcceptanceStep.전체_알림을_읽음_처리한다
import com.koddy.server.acceptance.notification.NotificationAcceptanceStep.특정_타입의_알림_ID를_조회한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.notification.domain.model.NotificationType
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 알림 조회 + 읽음 처리")
internal class NotificationAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private val menteeFixture = menteeFixture(sequence = 1)
        private val mentorFixture = mentorFixture(sequence = 1)
    }

    private lateinit var mentor: AuthMember
    private lateinit var mentee: AuthMember
    private var coffeeChatId: Long = 0

    @BeforeEach
    override fun setUp() {
        mentor = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
        mentee = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
        coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
            menteeId = mentee.id,
            accessToken = mentor.token.accessToken,
        )
        멘티가_멘토의_커피챗_제안을_1차_수락한다(
            coffeeChatId = coffeeChatId,
            start = "2024/2/5-18:00".toLocalDateTime(),
            end = "2024/2/5-18:30".toLocalDateTime(),
            accessToken = mentee.token.accessToken,
        )
        멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(
            coffeeChatId = coffeeChatId,
            accessToken = mentor.token.accessToken,
        )
    }

    @Nested
    @DisplayName("멘토 알림 조회 + 읽음 처리")
    internal inner class MentorReadAndProcessing {
        @Test
        fun `멘토가 알림을 조회하고 단건 읽음 처리를 진행한다`() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false),
                types = listOf(MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTEE_PENDING),
                hasNext = false,
            )

            // Read Single Notification
            val notificationId = 특정_타입의_알림_ID를_조회한다(
                type = MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW,
                page = 1,
                accessToken = mentor.token.accessToken,
            )
            단건_알림을_읽음_처리한다(
                notificationId = notificationId,
                accessToken = mentor.token.accessToken,
            )

            val response2: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response2,
                reads = listOf(true),
                types = listOf(MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTEE_PENDING),
                hasNext = false,
            )
        }

        @Test
        fun `멘토가 알림을 조회하고 전체 읽음 처리를 진행한다`() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false),
                types = listOf(MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTEE_PENDING),
                hasNext = false,
            )

            // Read All Notification
            전체_알림을_읽음_처리한다(accessToken = mentor.token.accessToken)

            val response2: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response2,
                reads = listOf(true),
                types = listOf(MENTOR_RECEIVE_MENTEE_PENDING_FROM_MENTOR_FLOW),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTEE_PENDING),
                hasNext = false,
            )
        }
    }

    @Nested
    @DisplayName("멘티 알림 조회 + 읽음 처리")
    internal inner class MenteeReadAndProcessing {
        @Test
        fun `멘티가 알림을 조회하고 단건 읽음 처리를 진행한다`() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false, false),
                types = listOf(MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW, MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW),
                memberIds = listOf(mentor.id, mentor.id),
                coffeeChatIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )

            // Read Single Notification
            val notificationId1 = 특정_타입의_알림_ID를_조회한다(
                type = MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW,
                page = 1,
                accessToken = mentee.token.accessToken,
            )
            단건_알림을_읽음_처리한다(notificationId1, mentee.token.accessToken)

            val response2: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response2,
                reads = listOf(true, false),
                types = listOf(MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW, MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW),
                memberIds = listOf(mentor.id, mentor.id),
                coffeeChatIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )

            // Read Single Notification
            val notificationId2 = 특정_타입의_알림_ID를_조회한다(
                type = MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW,
                page = 1,
                accessToken = mentee.token.accessToken,
            )
            단건_알림을_읽음_처리한다(notificationId2, mentee.token.accessToken)

            val response3: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response3,
                reads = listOf(true, true),
                types = listOf(MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW, MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW),
                memberIds = listOf(mentor.id, mentor.id),
                coffeeChatIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )
        }

        @Test
        fun `멘티가 알림을 조회하고 전체 읽음 처리를 진행한다`() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false, false),
                types = listOf(MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW, MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW),
                memberIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatIds = listOf(mentor.id, mentor.id),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )

            // Read All Notification
            전체_알림을_읽음_처리한다(accessToken = mentee.token.accessToken)

            val response2: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response2,
                reads = listOf(true, true),
                types = listOf(MENTEE_RECEIVE_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW, MENTEE_RECEIVE_MENTOR_SUGGEST_FROM_MENTOR_FLOW),
                memberIds = listOf(mentor.id, mentor.id),
                coffeeChatIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )
        }
    }

    private fun assertNotificationsMatch(
        response: ValidatableResponse,
        reads: List<Boolean>,
        types: List<NotificationType>,
        memberIds: List<Long>,
        coffeeChatIds: List<Long>,
        coffeeChatStatusSnapshots: List<CoffeeChatStatus>,
        hasNext: Boolean,
    ) {
        response
            .body("result", hasSize<Int>(reads.size))
            .body("hasNext", `is`(hasNext))

        reads.indices.forEach { index ->
            response
                .body("result[$index].isRead", `is`(reads[index]))
                .body("result[$index].type", `is`(types[index].name))
                .body("result[$index].member.id", `is`(memberIds[index].toInt()))
                .body("result[$index].coffeeChat.id", `is`(coffeeChatIds[index].toInt()))
                .body("result[$index].coffeeChat.statusSnapshot", `is`(coffeeChatStatusSnapshots[index].name))
        }
    }
}
