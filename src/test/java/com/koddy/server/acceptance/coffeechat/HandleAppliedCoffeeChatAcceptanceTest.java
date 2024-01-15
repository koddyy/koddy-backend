package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.common.fixture.StrategyFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_거절한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_수락한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 멘티가 신청한 커피챗 처리")
public class HandleAppliedCoffeeChatAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘티가 신청한 커피챗 거절 API")
    class Reject {
        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentor.id(),
                    mentee.token().accessToken()
            );

            멘토가_멘티의_커피챗_신청을_거절한다(
                    coffeeChatId,
                    "거절..",
                    mentee.token().accessToken()
            ).statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘토는 멘티가 신청한 커피챗을 거절한다")
        void success() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentor.id(),
                    mentee.token().accessToken()
            );

            멘토가_멘티의_커피챗_신청을_거절한다(
                    coffeeChatId,
                    "거절..",
                    mentor.token().accessToken()
            ).statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("멘티가 신청한 커피챗 수락 API")
    class Approve {
        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentor.id(),
                    mentee.token().accessToken()
            );

            멘토가_멘티의_커피챗_신청을_수락한다(
                    coffeeChatId,
                    StrategyFixture.KAKAO_ID,
                    mentee.token().accessToken()
            ).statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘토는 멘티가 신청한 커피챗을 수락한다")
        void success() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            final long coffeeChatId = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentor.id(),
                    mentee.token().accessToken()
            );

            멘토가_멘티의_커피챗_신청을_수락한다(
                    coffeeChatId,
                    StrategyFixture.KAKAO_ID,
                    mentor.token().accessToken()
            ).statusCode(NO_CONTENT.value());
        }
    }
}
