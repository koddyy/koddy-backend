package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 커피챗 제안/신청")
public class CreateCoffeeChatAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘토 -> 멘티 커피챗 제안 API")
    class SuggestCoffeeChat {
        @Test
        @DisplayName("멘티는 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();

            멘토가_멘티에게_커피챗을_제안한다(mentee.id(), mentee.token().accessToken())
                    .statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("멘토가 멘티에게 커피챗을 제안한다")
        void success() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();

            멘토가_멘티에게_커피챗을_제안한다(mentee.id(), mentor.token().accessToken())
                    .statusCode(OK.value())
                    .body("coffeeChatId", notNullValue(Long.class));
        }
    }

    @Nested
    @DisplayName("멘티 -> 멘토 커피챗 신청 API")
    class ApplyCoffeeChat {
        @Test
        @DisplayName("멘토는 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();

            멘티가_멘토에게_커피챗을_신청한다(
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentor.id(),
                    mentor.token().accessToken()
            ).statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("이미 예약되었거나 멘토링이 가능하지 않은 날짜면 예외가 발생한다")
        void throwExceptionByCannotReservation() {
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();

            멘티가_멘토에게_커피챗을_신청한다(
                    LocalDateTime.of(2024, 2, 1, 18, 0),
                    LocalDateTime.of(2024, 2, 1, 19, 0),
                    mentor.id(),
                    mentee.token().accessToken()
            ).statusCode(CONFLICT.value())
                    .body("errorCode", is(CANNOT_RESERVATION.getErrorCode()))
                    .body("message", is(CANNOT_RESERVATION.getMessage()));
        }

        @Test
        @DisplayName("멘티가 멘토에게 커피챗을 신청한다")
        void success() {
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();

            멘티가_멘토에게_커피챗을_신청한다(
                    LocalDateTime.of(2024, 2, 5, 13, 0),
                    LocalDateTime.of(2024, 2, 5, 13, 30),
                    mentor.id(),
                    mentee.token().accessToken()
            ).statusCode(OK.value())
                    .body("coffeeChatId", notNullValue(Long.class));
        }
    }
}
