package com.koddy.server.auth.infrastructure.oauth;

import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthProperties;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.koddy.server.common.utils.TokenUtils;
import com.koddy.server.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.OAuthUtils.AUTHORIZATION_CODE;
import static com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI;
import static com.koddy.server.common.utils.OAuthUtils.STATE;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.BEARER_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.EXPIRES_IN;
import static com.koddy.server.common.utils.TokenUtils.ID_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@EnableConfigurationProperties(value = {
        GoogleOAuthProperties.class
})
@SpringBootTest(classes = {
        GoogleOAuthConnector.class
})
@DisplayName("Auth -> OAuthConnector [구글] 테스트")
class OAuthConnectorTest {
    @Autowired
    private GoogleOAuthConnector sut;

    @Autowired
    private GoogleOAuthProperties properties;

    @MockBean
    private RestTemplate restTemplate;

    @Nested
    @DisplayName("Token 응답받기")
    class FetchToken {
        @Test
        @DisplayName("Google Server와의 통신 불량으로 인해 예외가 발생한다")
        void failure() {
            // given
            given(restTemplate.postForEntity(
                    eq(properties.tokenUrl()),
                    any(HttpEntity.class),
                    eq(GoogleTokenResponse.class)
            )).willThrow(RestClientException.class);

            // when - then
            assertThatThrownBy(() -> sut.fetchToken(AUTHORIZATION_CODE, REDIRECT_URI, STATE))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(UNEXPECTED_SERVER_ERROR.getMessage());
        }

        @Test
        @DisplayName("Authorization Code & RedirectUrl를 통해서 Google Authorization Server로부터 Token을 응답받는다")
        void success() {
            // given
            final GoogleTokenResponse response = TokenUtils.createGoogleTokenResponse();
            final ResponseEntity<GoogleTokenResponse> responseEntity = ResponseEntity.ok(response);
            given(restTemplate.postForEntity(
                    eq(properties.tokenUrl()),
                    any(HttpEntity.class),
                    eq(GoogleTokenResponse.class)
            )).willReturn(responseEntity);

            // when
            final GoogleTokenResponse result = (GoogleTokenResponse) sut.fetchToken(AUTHORIZATION_CODE, REDIRECT_URI, STATE);

            // then
            assertAll(
                    () -> assertThat(result.tokenType()).isEqualTo(BEARER_TOKEN),
                    () -> assertThat(result.idToken()).isEqualTo(ID_TOKEN),
                    () -> assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN),
                    () -> assertThat(result.expiresIn()).isEqualTo(EXPIRES_IN)
            );
        }
    }

    @Nested
    @DisplayName("사용자 정보 응답받기")
    class FetchUserInfo {
        @Test
        @DisplayName("Google Server와의 통신 불량으로 인해 예외가 발생한다")
        void failure() {
            // given
            given(restTemplate.exchange(
                    eq(properties.userInfoUrl()),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(GoogleUserResponse.class)
            )).willThrow(RestClientException.class);

            // when - then
            assertThatThrownBy(() -> sut.fetchUserInfo(ACCESS_TOKEN))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(UNEXPECTED_SERVER_ERROR.getMessage());
        }

        @Test
        @DisplayName("Access Token을 통해서 Google Server에 저장된 사용자 정보를 응답받는다")
        void success() {
            // given
            final GoogleUserResponse response = MENTOR_1.toGoogleUserResponse();
            final ResponseEntity<GoogleUserResponse> responseEntity = ResponseEntity.ok(response);
            given(restTemplate.exchange(
                    eq(properties.userInfoUrl()),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(GoogleUserResponse.class)
            )).willReturn(responseEntity);

            // when
            final GoogleUserResponse result = (GoogleUserResponse) sut.fetchUserInfo(ACCESS_TOKEN);

            // then
            assertAll(
                    () -> assertThat(result.name()).isEqualTo(MENTOR_1.getName()),
                    () -> assertThat(result.email()).isEqualTo(MENTOR_1.getEmail().getValue()),
                    () -> assertThat(result.profileImageUrl()).isEqualTo(MENTOR_1.getProfileImageUrl())
            );
        }
    }
}
