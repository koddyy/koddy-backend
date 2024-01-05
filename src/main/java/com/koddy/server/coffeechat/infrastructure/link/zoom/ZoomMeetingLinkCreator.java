package com.koddy.server.coffeechat.infrastructure.link.zoom;

import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthProperties;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkCreator;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
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

import static com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider.ZOOM;
import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoomMeetingLinkCreator implements MeetingLinkCreator {
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
        return fetchMeetingLinkInfo(request).getBody();
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
    public void delete(final String accessToken, final String meetingId) {
        final HttpHeaders headers = createMeetingLinkRequestHeader(accessToken);
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        deleteMeetingLink(request, meetingId);
    }

    private void deleteMeetingLink(final HttpEntity<Void> request, final String meetingId) {
        try {
            restTemplate.exchange(properties.other().deleteMeetingUrl(), DELETE, request, Void.class, meetingId);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}
