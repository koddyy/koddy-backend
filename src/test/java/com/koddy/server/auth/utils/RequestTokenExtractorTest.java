package com.koddy.server.auth.utils;

import com.koddy.server.common.UnitTest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.TOKEN_TYPE;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Auth -> RequestTokenExtractor 테스트")
class RequestTokenExtractorTest extends UnitTest {
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Nested
    @DisplayName("AccessToken 추출")
    class ExtractAccessToken {
        @Test
        @DisplayName("HTTP Request Message의 Authorization Header에 토큰이 없다면 Optional 빈 값을 응답한다")
        void emptyToken() {
            // given
            given(request.getHeader(ACCESS_TOKEN_HEADER)).willReturn(null);

            // when
            final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

            // then
            assertThat(token).isEmpty();
        }

        @Test
        @DisplayName("HTTP Request Message의 Authorization Header에 토큰 타입만 명시되었다면 Optional 빈 값을 응답한다")
        void emptyTokenWithType() {
            // given
            given(request.getHeader(ACCESS_TOKEN_HEADER)).willReturn(TOKEN_TYPE);

            // when
            final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

            // then
            assertThat(token).isEmpty();
        }

        @Test
        @DisplayName("HTTP Request Message의 Authorization Header에 토큰이 있다면 Optional로 감싸서 응답한다")
        void success() {
            // given
            given(request.getHeader(ACCESS_TOKEN_HEADER)).willReturn(String.join(" ", TOKEN_TYPE, ACCESS_TOKEN));

            // when
            final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

            // then
            assertAll(
                    () -> assertThat(token).isPresent(),
                    () -> assertThat(token.get()).isEqualTo(ACCESS_TOKEN)
            );
        }
    }

    @Nested
    @DisplayName("RefreshToken 추출")
    class ExtractRefreshToken {
        @Test
        @DisplayName("HTTP Request Message의 Cookie에 RefreshToken이 없다면 Optional 빈 값을 응답한다")
        void emptyToken() {
            // given
            given(request.getCookies()).willReturn(new Cookie[0]);

            // when
            final Optional<String> token = RequestTokenExtractor.extractRefreshToken(request);

            // then
            assertThat(token).isEmpty();
        }

        @Test
        @DisplayName("HTTP Request Message의 Cookie에 RefreshToken이 있다면 Optional로 감싸서 응답한다")
        void success() {
            // given
            given(request.getCookies()).willReturn(new Cookie[]{new Cookie(REFRESH_TOKEN_HEADER, REFRESH_TOKEN)});

            // when
            final Optional<String> token = RequestTokenExtractor.extractRefreshToken(request);

            // then
            assertAll(
                    () -> assertThat(token).isPresent(),
                    () -> assertThat(token.get()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }
}
