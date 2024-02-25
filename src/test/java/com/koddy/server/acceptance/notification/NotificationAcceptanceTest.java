package com.koddy.server.acceptance.notification;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.notification.domain.model.NotificationType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다;
import static com.koddy.server.acceptance.notification.NotificationAcceptanceStep.단건_알림을_읽음_처리한다;
import static com.koddy.server.acceptance.notification.NotificationAcceptanceStep.알림을_조회한다;
import static com.koddy.server.acceptance.notification.NotificationAcceptanceStep.전체_알림을_읽음_처리한다;
import static com.koddy.server.acceptance.notification.NotificationAcceptanceStep.특정_타입의_알림_ID를_조회한다;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL;
import static com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST;
import static com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 알림 조회 + 읽음 처리")
public class NotificationAcceptanceTest extends AcceptanceTest {
    private AuthMember mentor;
    private AuthMember mentee;
    private long coffeeChatId;

    @BeforeEach
    void setUp() {
        mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
        mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
        coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
        멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId,
                LocalDateTime.of(2024, 2, 5, 18, 0),
                LocalDateTime.of(2024, 2, 5, 18, 30),
                mentee.token().accessToken()
        );
        멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(
                coffeeChatId,
                "취소..",
                mentor.token().accessToken()
        );
    }

    @Nested
    @DisplayName("알림 조회 + 읽음 처리")
    class ReadAndProcessing {
        @Test
        @DisplayName("멘토가 알림을 조회하고 단건 읽음 처리를 진행한다")
        void mentorReadAndSingleProcessing() {
            final ValidatableResponse response1 = 알림을_조회한다(1, mentor.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response1,
                    List.of(false),
                    List.of(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                    List.of(mentee.id()),
                    List.of(coffeeChatId),
                    false
            );

            final long notificationId = 특정_타입의_알림_ID를_조회한다(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING, 1, mentor.token().accessToken());
            단건_알림을_읽음_처리한다(notificationId, mentor.token().accessToken());

            final ValidatableResponse response2 = 알림을_조회한다(1, mentor.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response2,
                    List.of(true),
                    List.of(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                    List.of(mentee.id()),
                    List.of(coffeeChatId),
                    false
            );
        }

        @Test
        @DisplayName("멘토가 알림을 조회하고 전체 읽음 처리를 진행한다")
        void mentorReadAndAllProcessing() {
            final ValidatableResponse response1 = 알림을_조회한다(1, mentor.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response1,
                    List.of(false),
                    List.of(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                    List.of(mentee.id()),
                    List.of(coffeeChatId),
                    false
            );

            전체_알림을_읽음_처리한다(mentor.token().accessToken());

            final ValidatableResponse response2 = 알림을_조회한다(1, mentor.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response2,
                    List.of(true),
                    List.of(MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                    List.of(mentee.id()),
                    List.of(coffeeChatId),
                    false
            );
        }

        @Test
        @DisplayName("멘티가 알림을 조회하고 단건 읽음 처리를 진행한다")
        void menteeReadAndSingleProcessing() {
            final ValidatableResponse response1 = 알림을_조회한다(1, mentee.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response1,
                    List.of(false, false),
                    List.of(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                    List.of(mentor.id(), mentor.id()),
                    List.of(coffeeChatId, coffeeChatId),
                    false
            );

            final long notificationId1 = 특정_타입의_알림_ID를_조회한다(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, 1, mentee.token().accessToken());
            단건_알림을_읽음_처리한다(notificationId1, mentee.token().accessToken());

            final ValidatableResponse response2 = 알림을_조회한다(1, mentee.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response2,
                    List.of(true, false),
                    List.of(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                    List.of(mentor.id(), mentor.id()),
                    List.of(coffeeChatId, coffeeChatId),
                    false
            );

            final long notificationId2 = 특정_타입의_알림_ID를_조회한다(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST, 1, mentee.token().accessToken());
            단건_알림을_읽음_처리한다(notificationId2, mentee.token().accessToken());

            final ValidatableResponse response3 = 알림을_조회한다(1, mentee.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response3,
                    List.of(true, true),
                    List.of(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                    List.of(mentor.id(), mentor.id()),
                    List.of(coffeeChatId, coffeeChatId),
                    false
            );
        }

        @Test
        @DisplayName("멘티가 알림을 조회하고 전체 읽음 처리를 진행한다")
        void menteeReadAndAllProcessing() {
            final ValidatableResponse response1 = 알림을_조회한다(1, mentee.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response1,
                    List.of(false, false),
                    List.of(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                    List.of(coffeeChatId, coffeeChatId),
                    List.of(mentor.id(), mentor.id()),
                    false
            );

            전체_알림을_읽음_처리한다(mentee.token().accessToken());

            final ValidatableResponse response2 = 알림을_조회한다(1, mentee.token().accessToken()).statusCode(OK.value());
            assertNotificationsMatch(
                    response2,
                    List.of(true, true),
                    List.of(MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL, MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                    List.of(mentor.id(), mentor.id()),
                    List.of(coffeeChatId, coffeeChatId),
                    false
            );
        }
    }

    private void assertNotificationsMatch(
            final ValidatableResponse response,
            final List<Boolean> reads,
            final List<NotificationType> types,
            final List<Long> memberIds,
            final List<Long> coffeeChatIds,
            final boolean hasNext
    ) {
        final int totalSize = reads.size();
        response
                .body("result", hasSize(totalSize))
                .body("hasNext", is(hasNext));

        for (int i = 0; i < totalSize; i++) {
            final String index = String.format("result[%d]", i);
            final boolean read = reads.get(i);
            final NotificationType type = types.get(i);
            final long memberId = memberIds.get(i);
            final long coffeeChatId = coffeeChatIds.get(i);

            response
                    .body(index + ".read", is(read))
                    .body(index + ".type", is(type.name()))
                    .body(index + ".member.id", is((int) memberId))
                    .body(index + ".coffeeChat.id", is((int) coffeeChatId));
        }
    }
}
