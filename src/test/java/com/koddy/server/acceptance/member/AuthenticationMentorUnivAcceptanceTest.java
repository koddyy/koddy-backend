package com.koddy.server.acceptance.member;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토가_메일을_통해서_학교_인증을_시도한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토가_증명자료를_통해서_학교_인증을_시도한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토가_학교_메일로_발송된_인증번호를_제출한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.auth.exception.AuthExceptionCode.TOO_MANY_MAIL_AUTH_ATTEMPTS;
import static com.koddy.server.common.config.BlackboxLogicControlConfig.AUTH_CODE;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        RedisCleanerEachCallbackExtension.class
})
@DisplayName("[Acceptance Test] 멘토 학교 인증")
public class AuthenticationMentorUnivAcceptanceTest extends AcceptanceTest {
    private static final String SCHOOL_MAIL = "sjiwon@kyonggi.ac.kr";

    @Nested
    @DisplayName("메일 인증 시도 API")
    class AuthWithMail {
        @Test
        @DisplayName("학교 인증은 멘토 대상이므로 멘티는 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final String accessToken = MENTEE_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken)
                    .statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("메일을 통해서 학교 인증을 시도한다")
        void success() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken)
                    .statusCode(NO_CONTENT.value());
        }

        @Test
        @DisplayName("짧은 시간동안 3회 이상 인증 시도를 하게 되면 10분동안 인증 시도 밴을 당한다 (HTTP Status 429)")
        void throwExceptionByTooManyRequest() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken)
                    .statusCode(NO_CONTENT.value());
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken)
                    .statusCode(NO_CONTENT.value());
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken)
                    .statusCode(NO_CONTENT.value());
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken)
                    .statusCode(TOO_MANY_REQUESTS.value())
                    .body("errorCode", is(TOO_MANY_MAIL_AUTH_ATTEMPTS.getErrorCode()))
                    .body("message", is(TOO_MANY_MAIL_AUTH_ATTEMPTS.getMessage()));
        }
    }

    @Nested
    @DisplayName("메일 인증 확인 API")
    class ConfirmMailAuthCode {
        @Test
        @DisplayName("인증번호가 일치하지 않으면 인증에 실패한다")
        void throwExceptionByInvalidAuthCode() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken);
            멘토가_학교_메일로_발송된_인증번호를_제출한다(SCHOOL_MAIL, AUTH_CODE + "7", accessToken)
                    .statusCode(CONFLICT.value())
                    .body("errorCode", is(INVALID_AUTH_CODE.getErrorCode()))
                    .body("message", is(INVALID_AUTH_CODE.getMessage()));
        }

        @Test
        @DisplayName("인증번호가 일치하면 인증에 성공한다")
        void success() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토가_메일을_통해서_학교_인증을_시도한다(SCHOOL_MAIL, accessToken);
            멘토가_학교_메일로_발송된_인증번호를_제출한다(SCHOOL_MAIL, AUTH_CODE, accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("증명자료 인증 시도 API")
    class AuthWithProofData {
        @Test
        @DisplayName("학교 인증은 멘토 대상이므로 멘티는 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final String accessToken = MENTEE_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토가_증명자료를_통해서_학교_인증을_시도한다(accessToken)
                    .statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("증명자료를 통해서 학교 인증을 시도한다")
        void success() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토가_증명자료를_통해서_학교_인증을_시도한다(accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
