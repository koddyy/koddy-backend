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

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] Pending 상태인 커피챗에 대한 멘토의 최종 결정")
public class HandlePendingCoffeeChatAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("최종 거절 API")
    class Reject {
        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChatId,
                    LocalDateTime.of(2024, 2, 5, 13, 0),
                    LocalDateTime.of(2024, 2, 5, 13, 30),
                    mentee.token().accessToken()
            );

            멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다(
                    coffeeChatId,
                    "거절..",
                    mentee.token().accessToken()
            ).statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘토는 Pending 상태인 커피챗에 대해서 최종 거절한다")
        void success() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChatId,
                    LocalDateTime.of(2024, 2, 5, 13, 0),
                    LocalDateTime.of(2024, 2, 5, 13, 30),
                    mentee.token().accessToken()
            );

            멘토가_Pending_상태인_커피챗에_대해서_최종_거절을_한다(
                    coffeeChatId,
                    "거절..",
                    mentor.token().accessToken()
            ).statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("최종 수락 API")
    class Approve {
        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChatId,
                    LocalDateTime.of(2024, 2, 5, 13, 0),
                    LocalDateTime.of(2024, 2, 5, 13, 30),
                    mentee.token().accessToken()
            );

            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(
                    coffeeChatId,
                    StrategyFixture.KAKAO_ID,
                    mentee.token().accessToken()
            ).statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘토는 Pending 상태인 커피챗에 대해서 최종 수락한다")
        void success() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final long coffeeChatId = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentor.token().accessToken());
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                    coffeeChatId,
                    LocalDateTime.of(2024, 2, 5, 13, 0),
                    LocalDateTime.of(2024, 2, 5, 13, 30),
                    mentee.token().accessToken()
            );

            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(
                    coffeeChatId,
                    StrategyFixture.KAKAO_ID,
                    mentor.token().accessToken()
            ).statusCode(NO_CONTENT.value());
        }
    }
}
