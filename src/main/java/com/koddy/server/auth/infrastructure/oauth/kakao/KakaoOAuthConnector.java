package com.koddy.server.auth.infrastructure.oauth.kakao;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.infrastructure.oauth.kakao.response.KakaoTokenResponse;
import com.koddy.server.auth.infrastructure.oauth.kakao.response.KakaoUserResponse;
import com.koddy.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.KAKAO;
import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthConnector implements OAuthConnector {
    private final KakaoOAuthProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == KAKAO;
    }

    @Override
    public OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state) {
        final HttpHeaders headers = createTokenRequestHeader();
        final MultiValueMap<String, String> params = applyTokenRequestParams(code, redirectUri, state);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return fetchKakaoToken(request).getBody();
    }

    private HttpHeaders createTokenRequestHeader() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        return headers;
    }

    private MultiValueMap<String, String> applyTokenRequestParams(
            final String code,
            final String redirectUri,
            final String state
    ) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", properties.grantType());
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("state", state);
        params.add("client_id", properties.clientId());
        params.add("client_secret", properties.clientSecret());
        return params;
    }

    private ResponseEntity<KakaoTokenResponse> fetchKakaoToken(final HttpEntity<MultiValueMap<String, String>> request) {
        try {
            return restTemplate.postForEntity(properties.tokenUrl(), request, KakaoTokenResponse.class);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    @Override
    public OAuthUserResponse fetchUserInfo(final String accessToken) {
        final HttpHeaders headers = createUserInfoRequestHeader(accessToken);
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        return fetchKakaoUserInfo(request).getBody();
    }

    private HttpHeaders createUserInfoRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        headers.set(AUTHORIZATION, String.join(" ", TOKEN_TYPE, accessToken));
        return headers;
    }

    private ResponseEntity<KakaoUserResponse> fetchKakaoUserInfo(final HttpEntity<Void> request) {
        try {
            return restTemplate.exchange(properties.userInfoUrl(), GET, request, KakaoUserResponse.class);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}
