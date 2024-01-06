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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {
        GoogleOAuthUriGenerator.class,
        KakaoOAuthUriGenerator.class,
        ZoomOAuthUriGenerator.class
})
@EnableConfigurationProperties(value = {
        GoogleOAuthProperties.class,
        KakaoOAuthProperties.class,
        ZoomOAuthProperties.class
})
@DisplayName("Auth -> OAuthUriGenerator [구글, 카카오, 줌] 테스트")
class OAuthUriGeneratorTest {
    @Autowired
    private GoogleOAuthUriGenerator googleOAuthUri;

    @Autowired
    private GoogleOAuthProperties googleOAuthProperties;

    @Autowired
    private KakaoOAuthUriGenerator kakaoOAuthUri;

    @Autowired
    private KakaoOAuthProperties kakaoOAuthProperties;

    @Autowired
    private ZoomOAuthUriGenerator zoomOAuthUri;

    @Autowired
    private ZoomOAuthProperties zoomOAuthProperties;

    @Test
    @DisplayName("Google's Authorization Code를 받기 위한 Authorization Code Request URI를 생성한다")
    void googleOAuthUri() {
        // when
        final String uri = googleOAuthUri.generate(googleOAuthProperties.redirectUri());

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
        final String uri = kakaoOAuthUri.generate(kakaoOAuthProperties.redirectUri());

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
        final String uri = zoomOAuthUri.generate(zoomOAuthProperties.redirectUri());

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
