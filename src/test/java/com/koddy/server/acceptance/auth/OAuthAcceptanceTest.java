package com.koddy.server.acceptance.auth;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.config.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.auth.AuthAcceptanceStep.Google_OAuth_로그인을_진행한다;
import static com.koddy.server.acceptance.auth.AuthAcceptanceStep.Google_OAuth_인증_URL를_생성한다;
import static com.koddy.server.acceptance.auth.AuthAcceptanceStep.로그아웃을_진행한다;
import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.OAuthFixture.GOOGLE_MENTOR_1;
import static com.koddy.server.common.utils.OAuthUtils.GOOGLE_PROVIDER;
import static com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI;
import static com.koddy.server.common.utils.OAuthUtils.STATE;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] OAuth 인증 관련")
public class OAuthAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("OAuth 인증 URL 요청 API")
    class QueryOAuthLink {
        @Test
        @DisplayName("Google OAuth 인증 URL을 요청한다")
        void success() {
            Google_OAuth_인증_URL를_생성한다(GOOGLE_PROVIDER, REDIRECT_URI)
                    .statusCode(OK.value())
                    .body("result", notNullValue(String.class));
        }
    }

    @Nested
    @DisplayName("OAuth 로그인 API")
    class OAuthLoginApi {
        @Test
        @DisplayName("DB에 이메일에 대한 사용자 정보가 없으면 OAuth UserInfo를 토대로 회원가입을 진행한다")
        void failure() {
            Google_OAuth_로그인을_진행한다(GOOGLE_PROVIDER, GOOGLE_MENTOR_1.getAuthorizationCode(), REDIRECT_URI, STATE)
                    .statusCode(NOT_FOUND.value())
                    .body("name", is(MENTOR_1.getName()))
                    .body("email", is(MENTOR_1.getEmail().getValue()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()));
        }

        @Test
        @DisplayName("DB에 이메일에 대한 사용자 정보가 있으면 로그인을 진행하고 Token을 발급받는다")
        void success() {
            MENTOR_1.회원가입_로그인_후_프로필을_완성시킨다();
            Google_OAuth_로그인을_진행한다(GOOGLE_PROVIDER, GOOGLE_MENTOR_1.getAuthorizationCode(), REDIRECT_URI, STATE)
                    .statusCode(OK.value())
                    .header(AUTHORIZATION, notNullValue(String.class))
                    .header(SET_COOKIE, notNullValue(String.class))
                    .cookie(COOKIE_REFRESH_TOKEN, notNullValue(String.class))
                    .body("id", notNullValue(Long.class))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()));
        }
    }

    @Nested
    @DisplayName("로그아웃 API")
    class LogoutApi {
        @Test
        @DisplayName("로그아웃을 진행한다")
        void success() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            로그아웃을_진행한다(accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
