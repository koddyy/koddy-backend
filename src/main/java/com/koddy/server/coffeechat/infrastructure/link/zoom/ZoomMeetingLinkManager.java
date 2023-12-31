package com.koddy.server.coffeechat.infrastructure.link.zoom;

import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthProperties;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.global.exception.GlobalException;
import com.koddy.server.global.utils.DefaultRedisStringOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider.ZOOM;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.ANONYMOUS_MEETING_LINK;
import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoomMeetingLinkManager implements MeetingLinkManager {
    /**
     * Key = User Create Meeting ID <br>
     * Value = User OAuth Access Token
     */
    private static final String USER_OAUTH_TOKEN_FROM_CREATE_MEETING = "USER_OAUTH_TOKEN:CREATE_MEETING:%s";

    private final ZoomOAuthProperties properties;
    private final RestTemplate restTemplate;
    private final DefaultRedisStringOperator redisOperator;

    @Override
    public boolean isSupported(final MeetingLinkProvider provider) {
        return provider == ZOOM;
    }

    @Override
    public ZoomMeetingLinkResponse create(final String accessToken, final ZoomMeetingLinkRequest meetingLinkRequest) {
        final HttpHeaders headers = createMeetingLinkRequestHeader(accessToken);
        final HttpEntity<ZoomMeetingLinkRequest> request = new HttpEntity<>(meetingLinkRequest, headers);

        final ZoomMeetingLinkResponse response = fetchMeetingLinkInfo(request).getBody();
        final String cacheKey = createCacheKey(response.id());
        redisOperator.save(cacheKey, accessToken, Duration.ofMinutes(10));
        return response;
    }

    private HttpHeaders createMeetingLinkRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, LINK_REQUEST_CONTENT_TYPE);
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TOKEN_TYPE, accessToken));
        return headers;
    }

    private ResponseEntity<ZoomMeetingLinkResponse> fetchMeetingLinkInfo(final HttpEntity<ZoomMeetingLinkRequest> request) {
        try {
            return restTemplate.postForEntity(properties.other().createMeetingUrl(), request, ZoomMeetingLinkResponse.class);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private String createCacheKey(final String suffix) {
        return String.format(USER_OAUTH_TOKEN_FROM_CREATE_MEETING, suffix);
    }

    @Override
    public void delete(final String meetingId) {
        final String cacheKey = createCacheKey(meetingId);
        if (!redisOperator.contains(cacheKey)) {
            throw new CoffeeChatException(ANONYMOUS_MEETING_LINK);
        }

        final String oAuthAccessToken = redisOperator.get(cacheKey);
        final HttpHeaders headers = createMeetingLinkRequestHeader(oAuthAccessToken);
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        deleteMeetingLink(request, meetingId);
    }

    private void deleteMeetingLink(final HttpEntity<Void> request, final String meetingId) {
        try {
            restTemplate.exchange(properties.other().deleteMeetingUrl(), DELETE, request, Void.class, meetingId);
            redisOperator.delete(createCacheKey(meetingId));
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}
