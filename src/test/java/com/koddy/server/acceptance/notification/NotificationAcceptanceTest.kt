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
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.notification.domain.model.NotificationType
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL
import com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST
import com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.OK
import java.time.LocalDateTime

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 알림 조회 + 읽음 처리")
internal class NotificationAcceptanceTest : AcceptanceTestKt() {
    private lateinit var mentor: AuthMember
    private lateinit var mentee: AuthMember
    private var coffeeChatId: Long = 0

    @BeforeEach
    override fun setUp() {
        mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
        mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
        coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentor.token.accessToken)
        멘티가_멘토의_커피챗_제안을_1차_수락한다(
            coffeeChatId,
            LocalDateTime.of(2024, 2, 5, 18, 0),
            LocalDateTime.of(2024, 2, 5, 18, 30),
            mentee.token.accessToken,
        )
        멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(
            coffeeChatId,
            "취소..",
            mentor.token.accessToken,
        )
    }

    @Nested
    @DisplayName("멘토 알림 조회 + 읽음 처리")
    internal inner class MentorReadAndProcessing {
        @Test
        @DisplayName("멘토가 알림을 조회하고 단건 읽음 처리를 진행한다")
        fun mentorReadAndSingleProcessing() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false),
                types = listOf(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(CoffeeChatStatus.MENTEE_PENDING),
                hasNext = false,
            )

            // Read Single Notification
            val notificationId = 특정_타입의_알림_ID를_조회한다(
                type = MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING,
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
                types = listOf(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(CoffeeChatStatus.MENTEE_PENDING),
                hasNext = false,
            )
        }

        @Test
        @DisplayName("멘토가 알림을 조회하고 전체 읽음 처리를 진행한다")
        fun mentorReadAndAllProcessing() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false),
                types = listOf(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(CoffeeChatStatus.MENTEE_PENDING),
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
                types = listOf(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                memberIds = listOf(mentee.id),
                coffeeChatIds = listOf(coffeeChatId),
                coffeeChatStatusSnapshots = listOf(CoffeeChatStatus.MENTEE_PENDING),
                hasNext = false,
            )
        }
    }

    @Nested
    @DisplayName("멘티 알림 조회 + 읽음 처리")
    internal inner class MenteeReadAndProcessing {
        @Test
        @DisplayName("멘티가 알림을 조회하고 단건 읽음 처리를 진행한다")
        fun menteeReadAndSingleProcessing() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false, false),
                types = listOf(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                memberIds = listOf(mentor.id, mentor.id),
                coffeeChatIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )

            // Read Single Notification
            val notificationId1 = 특정_타입의_알림_ID를_조회한다(
                type = MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL,
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
                types = listOf(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                memberIds = listOf(mentor.id, mentor.id),
                coffeeChatIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )

            // Read Single Notification
            val notificationId2 = 특정_타입의_알림_ID를_조회한다(
                type = MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST,
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
                types = listOf(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                memberIds = listOf(mentor.id, mentor.id),
                coffeeChatIds = listOf(coffeeChatId, coffeeChatId),
                coffeeChatStatusSnapshots = listOf(MENTOR_FINALLY_CANCEL, MENTOR_SUGGEST),
                hasNext = false,
            )
        }

        @Test
        @DisplayName("멘티가 알림을 조회하고 전체 읽음 처리를 진행한다")
        fun menteeReadAndAllProcessing() {
            // Unread Notification
            val response1: ValidatableResponse = 알림을_조회한다(
                page = 1,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
            assertNotificationsMatch(
                response = response1,
                reads = listOf(false, false),
                types = listOf(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
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
                types = listOf(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
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
            .body("result", hasSize<Any>(reads.size))
            .body("hasNext", `is`(hasNext))

        reads.indices.forEach { index ->
            response
                .body("result[$index].read", `is`(reads[index]))
                .body("result[$index].type", `is`(types[index].name))
                .body("result[$index].member.id", `is`(memberIds[index].toInt()))
                .body("result[$index].coffeeChat.id", `is`(coffeeChatIds[index].toInt()))
                .body("result[$index].coffeeChat.statusSnapshot", `is`(coffeeChatStatusSnapshots[index].name))
        }
    }
}
