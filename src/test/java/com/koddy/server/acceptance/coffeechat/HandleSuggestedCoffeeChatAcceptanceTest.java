package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_거절한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 멘토가 제안한 커피챗 처리")
public class HandleSuggestedCoffeeChatAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘토가 제안한 커피챗 거절 API")
    class Reject {
        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());

            멘티가_멘토의_커피챗_제안을_거절한다(
                    coffeeChatId,
                    "거절..",
                    mentor.token().accessToken()
            ).statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘티는 멘토가 제안한 커피챗을 거절한다")
        void success() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());

            멘티가_멘토의_커피챗_제안을_거절한다(
                    coffeeChatId,
                    "거절..",
                    mentee.token().accessToken()
            ).statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("멘토가 제안한 커피챗 1차 수락 API")
    class Pending {
        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());

            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChatId,
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentor.token().accessToken()
            ).statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘티는 멘토가 제안한 커피챗을 1차 수락한다")
        void success() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());

            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChatId,
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentee.token().accessToken()
            ).statusCode(NO_CONTENT.value());
        }
    }
}
