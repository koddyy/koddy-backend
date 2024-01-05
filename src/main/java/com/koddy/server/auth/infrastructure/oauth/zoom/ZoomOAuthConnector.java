package com.koddy.server.auth.infrastructure.oauth.zoom;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.infrastructure.oauth.zoom.response.ZoomTokenResponse;
import com.koddy.server.auth.infrastructure.oauth.zoom.response.ZoomUserResponse;
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

import java.util.Base64;

import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.ZOOM;
import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoomOAuthConnector implements OAuthConnector {
    private final ZoomOAuthProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == ZOOM;
    }

    @Override
    public OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state) {
        final HttpHeaders headers = createTokenRequestHeader();
        final MultiValueMap<String, String> params = applyTokenRequestParams(code, redirectUri, state);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return fetchZoomToken(request).getBody();
    }

    private HttpHeaders createTokenRequestHeader() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, OAUTH_CONTENT_TYPE);
        headers.set(AUTHORIZATION, String.join(" ", "Basic", getEncodedClientProperties()));
        return headers;
    }

    private String getEncodedClientProperties() {
        final String value = String.join(":", properties.clientId(), properties.clientSecret());
        final byte[] encode = Base64.getEncoder().encode(value.getBytes());
        return new String(encode, UTF_8);
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
        return params;
    }

    private ResponseEntity<ZoomTokenResponse> fetchZoomToken(final HttpEntity<MultiValueMap<String, String>> request) {
        try {
            return restTemplate.postForEntity(properties.tokenUrl(), request, ZoomTokenResponse.class);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    @Override
    public OAuthUserResponse fetchUserInfo(final String accessToken) {
        final HttpHeaders headers = createUserInfoRequestHeader(accessToken);
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        return fetchZoomUserInfo(request).getBody();
    }

    private HttpHeaders createUserInfoRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, OAUTH_CONTENT_TYPE);
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TOKEN_TYPE, accessToken));
        return headers;
    }

    private ResponseEntity<ZoomUserResponse> fetchZoomUserInfo(final HttpEntity<Void> request) {
        try {
            return restTemplate.exchange(properties.userInfoUrl(), GET, request, ZoomUserResponse.class);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}
