package com.koddy.server.acceptance.member;

import com.koddy.server.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.서비스를_탈퇴한다;
import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("[Acceptance Test] 사용자 계정 관리 기능")
public class ManageAccountAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("회원가입 + 로그인 API")
    class SignUpAndLoginApi {
        @Test
        @DisplayName("멘토 회원가입 + 로그인을 진행한다")
        void mentorSuccess() {
            멘토_회원가입_후_로그인을_진행한다(MENTOR_1)
                    .statusCode(OK.value())
                    .header(AUTHORIZATION, notNullValue(String.class))
                    .cookie(COOKIE_REFRESH_TOKEN, notNullValue(String.class))
                    .body("id", notNullValue(Long.class))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()));
        }

        @Test
        @DisplayName("멘티 회원가입 + 로그인을 진행한다")
        void menteeSuccess() {
            멘티_회원가입_후_로그인을_진행한다(MENTEE_1)
                    .statusCode(OK.value())
                    .header(AUTHORIZATION, notNullValue(String.class))
                    .cookie(COOKIE_REFRESH_TOKEN, notNullValue(String.class))
                    .body("id", notNullValue(Long.class))
                    .body("name", is(MENTEE_1.getName()))
                    .body("profileImageUrl", is(MENTEE_1.getProfileImageUrl()));
        }
    }

    @Nested
    @DisplayName("서비스 탈퇴 API")
    class DeleteApi {
        @Test
        @DisplayName("멘토가 서비스를 탈퇴한다")
        void mentorDelete() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            서비스를_탈퇴한다(accessToken)
                    .statusCode(NO_CONTENT.value());
        }

        @Test
        @DisplayName("멘티가 서비스를 탈퇴한다")
        void menteeDelete() {
            final String accessToken = MENTEE_1.회원가입_로그인_후_AccessToken을_추출한다();
            서비스를_탈퇴한다(accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
