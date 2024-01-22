package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 신청/제안한 커피챗 취소")
public class CancelCoffeeChatAcceptanceTest extends AcceptanceTest {
    private AuthMember mentorA;
    private AuthMember mentorB;
    private AuthMember menteeA;
    private AuthMember menteeB;

    @BeforeEach
    void setUp() {
        mentorA = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
        mentorB = MENTOR_2.회원가입과_로그인을_하고_프로필을_완성시킨다();
        menteeA = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
        menteeB = MENTEE_2.회원가입과_로그인을_하고_프로필을_완성시킨다();
    }

    @Nested
    @DisplayName("멘토가 제안한 커피챗 취소")
    class CancelSuggestedCoffeeChat {
        @Test
        @DisplayName("자신이 제안한 커피챗이 아니면 취소할 수 없다")
        void throwExceptionByInvalidPermission() {
            final long suggestedByMentorA = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(menteeA.id(), mentorA.token().accessToken());
            final long suggestedByMentorB = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(menteeA.id(), mentorB.token().accessToken());

            신청_제안한_커피챗을_취소한다(suggestedByMentorA, mentorB.token().accessToken())
                    .statusCode(NOT_FOUND.value())
                    .body("errorCode", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getErrorCode()))
                    .body("message", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage()));
            신청_제안한_커피챗을_취소한다(suggestedByMentorB, mentorA.token().accessToken())
                    .statusCode(NOT_FOUND.value())
                    .body("errorCode", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getErrorCode()))
                    .body("message", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("멘토 자신이 제안한 커피챗을 취소한다")
        void success() {
            final long suggestedByMentorA = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(menteeA.id(), mentorA.token().accessToken());
            final long suggestedByMentorB = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(menteeA.id(), mentorB.token().accessToken());

            신청_제안한_커피챗을_취소한다(suggestedByMentorA, mentorA.token().accessToken())
                    .statusCode(NO_CONTENT.value());
            신청_제안한_커피챗을_취소한다(suggestedByMentorB, mentorB.token().accessToken())
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("멘티가 신청한 커피챗 취소")
    class CancelAppliedCoffeeChat {
        @Test
        @DisplayName("자신이 신청한 커피챗이 아니면 취소할 수 없다")
        void throwExceptionByInvalidPermission() {
            final long appliedByMenteeA = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 5, 15, 0),
                    LocalDateTime.of(2024, 2, 5, 15, 30),
                    mentorA.id(),
                    menteeA.token().accessToken()
            );
            final long appliedByMenteeB = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 5, 17, 0),
                    LocalDateTime.of(2024, 2, 5, 17, 30),
                    mentorA.id(),
                    menteeB.token().accessToken()
            );

            신청_제안한_커피챗을_취소한다(appliedByMenteeA, menteeB.token().accessToken())
                    .statusCode(NOT_FOUND.value())
                    .body("errorCode", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getErrorCode()))
                    .body("message", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage()));
            신청_제안한_커피챗을_취소한다(appliedByMenteeB, menteeA.token().accessToken())
                    .statusCode(NOT_FOUND.value())
                    .body("errorCode", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getErrorCode()))
                    .body("message", is(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("멘티 자신이 신청한 커피챗을 취소한다")
        void success() {
            final long appliedByMenteeA = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 5, 15, 0),
                    LocalDateTime.of(2024, 2, 5, 15, 30),
                    mentorA.id(),
                    menteeA.token().accessToken()
            );
            final long appliedByMenteeB = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 5, 17, 0),
                    LocalDateTime.of(2024, 2, 5, 17, 30),
                    mentorA.id(),
                    menteeB.token().accessToken()
            );

            신청_제안한_커피챗을_취소한다(appliedByMenteeA, menteeA.token().accessToken())
                    .statusCode(NO_CONTENT.value());
            신청_제안한_커피챗을_취소한다(appliedByMenteeB, menteeB.token().accessToken())
                    .statusCode(NO_CONTENT.value());
        }
    }
}
