package com.koddy.server.auth.utils;

import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.ParallelTest;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Auth -> TokenProvider 테스트")
class TokenProviderTest extends ParallelTest {
    private static final String SECRET_KEY = "asldfjsadlfjalksjf01jf02j9012f0120f12jf1j29v0saduf012ue101212c01";

    private final TokenProvider invalidProvider = new TokenProvider(SECRET_KEY, 0L, 0L);
    private final TokenProvider validProvider = new TokenProvider(SECRET_KEY, 7200L, 7200L);

    private final Member<?> member = MENTOR_1.toDomain().apply(1L);

    @Test
    @DisplayName("AccessToken과 RefreshToken을 발급한다")
    void createToken() {
        // when
        final String accessToken = validProvider.createAccessToken(member.getId(), member.getRoleTypes());
        final String refreshToken = validProvider.createRefreshToken(member.getId());

        // then
        assertAll(
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(refreshToken).isNotNull()
        );
    }

    @Test
    @DisplayName("Token의 Payload를 추출한다")
    void extractPayload() {
        // when
        final String accessToken = validProvider.createAccessToken(member.getId(), member.getRoleTypes());

        // then
        assertThat(validProvider.getId(accessToken)).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("Token 만료에 대한 유효성을 검증한다")
    void validateToken1() {
        // when
        final String validToken = validProvider.createAccessToken(member.getId(), member.getRoleTypes());
        final String invalidToken = invalidProvider.createAccessToken(member.getId(), member.getRoleTypes());

        // then
        assertDoesNotThrow(() -> validProvider.validateToken(validToken));
        assertThatThrownBy(() -> invalidProvider.validateToken(invalidToken))
                .isInstanceOf(AuthException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("Token 조작에 대한 유효성을 검증한다")
    void validateToken2() {
        // when
        final String forgedToken = validProvider.createAccessToken(member.getId(), member.getRoleTypes()) + "hacked";

        // then
        assertThatThrownBy(() -> validProvider.validateToken(forgedToken))
                .isInstanceOf(AuthException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }
}
