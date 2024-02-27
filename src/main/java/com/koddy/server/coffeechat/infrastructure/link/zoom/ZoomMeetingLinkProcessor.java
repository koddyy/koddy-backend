package com.koddy.server.coffeechat.infrastructure.link.zoom;

import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthProperties;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkTokenCashier;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.coffeechat.infrastructure.link.MeetingLinkProcessor;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.global.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.ANONYMOUS_MEETING_LINK;
import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

@Component
public class ZoomMeetingLinkProcessor implements MeetingLinkProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ZoomOAuthProperties properties;
    private final RestTemplate restTemplate;
    private final MeetingLinkTokenCashier meetingLinkTokenCashier;

    public ZoomMeetingLinkProcessor(
            final ZoomOAuthProperties properties,
            final RestTemplate restTemplate,
            final MeetingLinkTokenCashier meetingLinkTokenCashier
    ) {
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.meetingLinkTokenCashier = meetingLinkTokenCashier;
    }

    @Override
    public MeetingLinkResponse create(final String oAuthAccessToken, final MeetingLinkRequest meetingLinkRequest) {
        final ZoomMeetingLinkResponse response = fetchMeetingLinkInfo(oAuthAccessToken, meetingLinkRequest);
        meetingLinkTokenCashier.storeViaMeetingId(response.id(), oAuthAccessToken, Duration.ofMinutes(10));
        return response;
    }

    private ZoomMeetingLinkResponse fetchMeetingLinkInfo(
            final String accessToken,
            final MeetingLinkRequest meetingLinkRequest
    ) {
        try {
            return restTemplate.exchange(
                    properties.getOther().getCreateMeetingUrl(),
                    POST,
                    new HttpEntity<>(meetingLinkRequest, createMeetingLinkRequestHeader(accessToken)),
                    ZoomMeetingLinkResponse.class
            ).getBody();
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private HttpHeaders createMeetingLinkRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, OAUTH_CONTENT_TYPE);
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TOKEN_TYPE, accessToken));
        return headers;
    }

    @Override
    public void delete(final String meetingId) {
        if (!meetingLinkTokenCashier.containsViaMeetingId(meetingId)) {
            throw new CoffeeChatException(ANONYMOUS_MEETING_LINK);
        }

        final String oAuthAccessToken = meetingLinkTokenCashier.getViaMeetingId(meetingId);
        deleteMeetingLink(oAuthAccessToken, meetingId);
    }

    private void deleteMeetingLink(final String oAuthAccessToken, final String meetingId) {
        try {
            restTemplate.exchange(
                    properties.getOther().getDeleteMeetingUrl(),
                    DELETE,
                    new HttpEntity<>(createMeetingLinkRequestHeader(oAuthAccessToken)),
                    Void.class,
                    meetingId
            );
            meetingLinkTokenCashier.deleteViaMeetingId(meetingId);
        } catch (final RestClientException e) {
            log.error("OAuth Error... ", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}
