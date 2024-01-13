package com.koddy.server.auth.infrastructure.social;

import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthConnector;
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse;
import com.koddy.server.auth.infrastructure.social.kakao.KakaoOAuthConnector;
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse;
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthConnector;
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse;
import com.koddy.server.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.KAKAO;
import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.ZOOM;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.OAuthUtils.AUTHORIZATION_CODE;
import static com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI;
import static com.koddy.server.common.utils.OAuthUtils.STATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Auth -> OAuthLoginProcessorFacade [구글, 카카오, 줌] 테스트")
class OAuthLoginProcessorFacadeTest extends UnitTest {
    private final GoogleOAuthConnector googleOAuthConnector = mock(GoogleOAuthConnector.class);
    private final KakaoOAuthConnector kakaoOAuthConnector = mock(KakaoOAuthConnector.class);
    private final ZoomOAuthConnector zoomOAuthConnector = mock(ZoomOAuthConnector.class);
    private final OAuthLoginProcessorFacade sut = new OAuthLoginProcessorFacade(
            googleOAuthConnector,
            kakaoOAuthConnector,
            zoomOAuthConnector
    );

    private final GoogleUserResponse googleUserResponse = MENTOR_1.toGoogleUserResponse();
    private final KakaoUserResponse kakaoUserResponse = MENTOR_1.toKakaoUserResponse();
    private final ZoomUserResponse zoomUserResponse = MENTOR_1.toZoomUserResponse();

    @Test
    @DisplayName("Provider별 로그인을 진행한다")
    void login() {
        // given
        given(googleOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE)).willReturn(googleUserResponse);
        given(kakaoOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE)).willReturn(kakaoUserResponse);
        given(zoomOAuthConnector.login(AUTHORIZATION_CODE, REDIRECT_URI, STATE)).willReturn(zoomUserResponse);

        // when
        final OAuthUserResponse google = sut.login(GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE);
        final OAuthUserResponse kakao = sut.login(KAKAO, AUTHORIZATION_CODE, REDIRECT_URI, STATE);
        final OAuthUserResponse zoom = sut.login(ZOOM, AUTHORIZATION_CODE, REDIRECT_URI, STATE);

        // when - then
        assertAll(
                () -> assertThat(google).isEqualTo(googleUserResponse),
                () -> assertThat(kakao).isEqualTo(kakaoUserResponse),
                () -> assertThat(zoom).isEqualTo(zoomUserResponse)
        );
    }
}
