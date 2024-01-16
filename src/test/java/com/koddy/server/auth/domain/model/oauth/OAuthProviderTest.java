package com.koddy.server.auth.domain.model.oauth;

import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.KAKAO;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> 도메인 [OAuthProvider] 테스트")
class OAuthProviderTest extends UnitTest {
    @Test
    @DisplayName("제공하지 않는 Provider에 대해서 OAuthProvider를 가져오려고 하면 예외가 발생한다")
    void throwExceptionByInvalidOAuthProvider() {
        assertThatThrownBy(() -> OAuthProvider.from("anonymous"))
                .isInstanceOf(AuthException.class)
                .hasMessage(INVALID_OAUTH_PROVIDER.getMessage());
    }

    @Test
    @DisplayName("주어진 Provider에 따른 OAuthProvider를 가져온다")
    void getSpecificOAuthProvider() {
        assertAll(
                () -> assertThat(OAuthProvider.from("google")).isEqualTo(GOOGLE),
                () -> assertThat(OAuthProvider.from("kakao")).isEqualTo(KAKAO)
        );
    }
}
