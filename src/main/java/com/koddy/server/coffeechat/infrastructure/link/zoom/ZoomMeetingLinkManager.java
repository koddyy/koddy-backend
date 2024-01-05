package com.koddy.server.coffeechat.infrastructure.link.zoom;

import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthProperties;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * Key: MeetingId, Value = AccessToken
     */
    private static final Map<String, String> tokenCache = new ConcurrentHashMap<>();

    private final ZoomOAuthProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public boolean isSupported(final MeetingLinkProvider provider) {
        return provider == ZOOM;
    }

    @Override
    public ZoomMeetingLinkResponse create(final String accessToken, final ZoomMeetingLinkRequest meetingLinkRequest) {
        final HttpHeaders headers = createMeetingLinkRequestHeader(accessToken);
        final HttpEntity<ZoomMeetingLinkRequest> request = new HttpEntity<>(meetingLinkRequest, headers);

        final ZoomMeetingLinkResponse response = fetchMeetingLinkInfo(request).getBody();
        tokenCache.put(response.id(), accessToken);
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

    @Override
    public void delete(final String meetingId) {
        if (!tokenCache.containsKey(meetingId)) {
            throw new CoffeeChatException(ANONYMOUS_MEETING_LINK);
        }

        final HttpHeaders headers = createMeetingLinkRequestHeader(tokenCache.get(meetingId));
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        deleteMeetingLink(request, meetingId);
    }

    private void deleteMeetingLink(final HttpEntity<Void> request, final String meetingId) {
        try {
            restTemplate.exchange(properties.other().deleteMeetingUrl(), DELETE, request, Void.class, meetingId);
            tokenCache.remove(meetingId);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}
