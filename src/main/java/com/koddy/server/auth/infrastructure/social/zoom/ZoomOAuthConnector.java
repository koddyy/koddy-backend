package com.koddy.server.auth.infrastructure.social.zoom;

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.infrastructure.social.OAuthConnector;
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomTokenResponse;
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse;
import com.koddy.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoomOAuthConnector implements OAuthConnector {
    private final ZoomOAuthProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state) {
        try {
            return restTemplate.exchange(
                    properties.tokenUrl(),
                    POST,
                    new HttpEntity<>(createTokenRequestParams(code, redirectUri, state), createTokenRequestHeader()),
                    ZoomTokenResponse.class
            ).getBody();
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private HttpHeaders createTokenRequestHeader() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, OAUTH_CONTENT_TYPE);
        headers.set(AUTHORIZATION, String.join(" ", "Basic", createEncodedAuthorizationHeaderWithClientProperties()));
        return headers;
    }

    private String createEncodedAuthorizationHeaderWithClientProperties() {
        final String value = String.join(":", properties.clientId(), properties.clientSecret());
        final byte[] encode = Base64.getEncoder().encode(value.getBytes());
        return new String(encode, UTF_8);
    }

    private MultiValueMap<String, String> createTokenRequestParams(
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

    @Override
    public OAuthUserResponse fetchUserInfo(final String accessToken) {
        try {
            return restTemplate.exchange(
                    properties.userInfoUrl(),
                    GET,
                    new HttpEntity<>(createUserInfoRequestHeader(accessToken)),
                    ZoomUserResponse.class
            ).getBody();
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private HttpHeaders createUserInfoRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, OAUTH_CONTENT_TYPE);
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TOKEN_TYPE, accessToken));
        return headers;
    }
}
