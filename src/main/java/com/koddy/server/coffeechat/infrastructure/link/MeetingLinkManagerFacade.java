package com.koddy.server.coffeechat.infrastructure.link;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.koddy.server.auth.infrastructure.oauth.zoom.ZoomOAuthConnector;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.coffeechat.infrastructure.link.zoom.ZoomMeetingLinkManager;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER;

@Component
@RequiredArgsConstructor
public class MeetingLinkManagerFacade implements MeetingLinkManager {
    private final ZoomOAuthConnector zoomOAuthConnector;
    private final ZoomMeetingLinkManager zoomMeetingLinkManager;
    private final GoogleOAuthConnector googleOAuthConnector;

    @Override
    public OAuthTokenResponse fetchToken(
            final OAuthProvider provider,
            final String code,
            final String redirectUri,
            final String state
    ) {
        return switch (provider) {
            case ZOOM -> zoomOAuthConnector.fetchToken(code, redirectUri, state);
            case GOOGLE -> googleOAuthConnector.fetchToken(code, redirectUri, state);
            case KAKAO -> throw new CoffeeChatException(INVALID_MEETING_LINK_PROVIDER);
        };
    }

    @Override
    public MeetingLinkResponse create(
            final MeetingLinkProvider provider,
            final String accessToken,
            final ZoomMeetingLinkRequest meetingLinkRequest
    ) {
        return switch (provider) {
            case ZOOM -> zoomMeetingLinkManager.create(accessToken, meetingLinkRequest);
            case GOOGLE -> throw new CoffeeChatException(INVALID_MEETING_LINK_PROVIDER);
        };
    }

    @Override
    public void delete(final MeetingLinkProvider provider, final String meetingId) {
        switch (provider) {
            case ZOOM -> zoomMeetingLinkManager.delete(meetingId);
            case GOOGLE -> throw new CoffeeChatException(INVALID_MEETING_LINK_PROVIDER);
        }
    }
}
