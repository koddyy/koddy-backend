package com.koddy.server.auth.infrastructure.social.google;

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.infrastructure.social.OAuthConnector;
import com.koddy.server.auth.infrastructure.social.google.response.GoogleTokenResponse;
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse;
import com.koddy.server.global.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class GoogleOAuthConnector implements OAuthConnector {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final GoogleOAuthProperties properties;
    private final RestTemplate restTemplate;

    public GoogleOAuthConnector(
            final GoogleOAuthProperties properties,
            final RestTemplate restTemplate
    ) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public OAuthTokenResponse fetchToken(final String code, final String redirectUri, final String state) {
        try {
            return restTemplate.exchange(
                    properties.tokenUrl(),
                    POST,
                    new HttpEntity<>(createTokenRequestParams(code, redirectUri, state), createTokenRequestHeader()),
                    GoogleTokenResponse.class
            ).getBody();
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private HttpHeaders createTokenRequestHeader() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, OAUTH_CONTENT_TYPE);
        return headers;
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
        params.add("client_id", properties.clientId());
        params.add("client_secret", properties.clientSecret());
        return params;
    }

    @Override
    public OAuthUserResponse fetchUserInfo(final String accessToken) {
        try {
            return restTemplate.exchange(
                    properties.userInfoUrl(),
                    GET,
                    new HttpEntity<>(createUserInfoRequestHeader(accessToken)),
                    GoogleUserResponse.class
            ).getBody();
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private HttpHeaders createUserInfoRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TOKEN_TYPE, accessToken));
        return headers;
    }
}
