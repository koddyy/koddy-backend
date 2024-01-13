package com.koddy.server.auth.infrastructure.oauth;

import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthProperties;
import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthUriGenerator;
import com.koddy.server.auth.infrastructure.oauth.kakao.KakaoOAuthProperties;
import com.koddy.server.auth.infrastructure.oauth.kakao.KakaoOAuthUriGenerator;
import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthProperties;
import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthUriGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.KAKAO;
import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.ZOOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {
        OAuthUriGeneratorFacade.class,
        GoogleOAuthUriGenerator.class,
        KakaoOAuthUriGenerator.class,
        ZoomOAuthUriGenerator.class
})
@EnableConfigurationProperties(value = {
        GoogleOAuthProperties.class,
        KakaoOAuthProperties.class,
        ZoomOAuthProperties.class
})
@DisplayName("Auth -> OAuthUriGeneratorFacade [구글, 카카오, 줌] 테스트")
class OAuthUriGeneratorFacadeTest {
    @Autowired
    private OAuthUriGeneratorFacade sut;

    @Autowired
    private GoogleOAuthProperties googleOAuthProperties;

    @Autowired
    private KakaoOAuthProperties kakaoOAuthProperties;

    @Autowired
    private ZoomOAuthProperties zoomOAuthProperties;

    @Test
    @DisplayName("Google's Authorization Code를 받기 위한 Authorization Code Request URI를 생성한다")
    void googleOAuthUri() {
        // when
        final String uri = sut.generate(GOOGLE, googleOAuthProperties.redirectUri());

        // then
        final MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(googleOAuthProperties.clientId()),
                () -> assertThat(queryParams.getFirst("scope")).isEqualTo(String.join(" ", googleOAuthProperties.scope())),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(googleOAuthProperties.redirectUri()),
                () -> assertThat(queryParams.getFirst("state")).isNotNull()
        );
    }

    @Test
    @DisplayName("Kakao's Authorization Code를 받기 위한 Authorization Code Request URI를 생성한다")
    void kakaoOAuthUri() {
        // when
        final String uri = sut.generate(KAKAO, kakaoOAuthProperties.redirectUri());

        // then
        final MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(kakaoOAuthProperties.clientId()),
                () -> assertThat(queryParams.getFirst("scope")).isEqualTo(String.join(" ", kakaoOAuthProperties.scope())),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(kakaoOAuthProperties.redirectUri()),
                () -> assertThat(queryParams.getFirst("state")).isNotNull()
        );
    }

    @Test
    @DisplayName("Zoom's Authorization Code를 받기 위한 Authorization Code Request URI를 생성한다")
    void zoomOAuthUri() {
        // when
        final String uri = sut.generate(ZOOM, zoomOAuthProperties.redirectUri());

        // then
        final MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(zoomOAuthProperties.clientId()),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(zoomOAuthProperties.redirectUri()),
                () -> assertThat(queryParams.getFirst("state")).isNotNull()
        );
    }
}
