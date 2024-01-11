package com.koddy.server.acceptance.auth;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.auth.AuthAcceptanceStep.토큰을_재발급받는다;
import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 토큰 재발급")
public class TokenReissueAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("토큰 재발급 API")
    class TokenReissueApi {
        @Test
        @DisplayName("유효한 RefreshToken을 통해서 AccessToken + RefreshToken을 재발급받는다")
        void success() {
            final String refreshToken = MENTOR_1.회원가입_로그인_후_RefreshToken을_추출한다();
            토큰을_재발급받는다(refreshToken)
                    .statusCode(NO_CONTENT.value())
                    .header(AUTHORIZATION, notNullValue(String.class))
                    .header(SET_COOKIE, notNullValue(String.class))
                    .cookie(COOKIE_REFRESH_TOKEN, notNullValue(String.class));
        }
    }
}