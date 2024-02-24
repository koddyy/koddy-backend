package com.koddy.server.coffeechat.infrastructure.link;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.infrastructure.social.google.GoogleOAuthConnector;
import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthConnector;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.coffeechat.infrastructure.link.zoom.ZoomMeetingLinkProcessor;
import org.springframework.stereotype.Component;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER;

@Component
public class MeetingLinkManagerFacade implements MeetingLinkManager {
    private final ZoomOAuthConnector zoomOAuthConnector;
    private final ZoomMeetingLinkProcessor zoomMeetingLinkProcessor;
    private final GoogleOAuthConnector googleOAuthConnector;

    public MeetingLinkManagerFacade(
            final ZoomOAuthConnector zoomOAuthConnector,
            final ZoomMeetingLinkProcessor zoomMeetingLinkProcessor,
            final GoogleOAuthConnector googleOAuthConnector
    ) {
        this.zoomOAuthConnector = zoomOAuthConnector;
        this.zoomMeetingLinkProcessor = zoomMeetingLinkProcessor;
        this.googleOAuthConnector = googleOAuthConnector;
    }

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
            final String oAuthAccessToken,
            final MeetingLinkRequest meetingLinkRequest
    ) {
        return switch (provider) {
            case ZOOM -> zoomMeetingLinkProcessor.create(oAuthAccessToken, meetingLinkRequest);
            case GOOGLE -> throw new UnsupportedOperationException("not supported yet...");
        };
    }

    @Override
    public void delete(final MeetingLinkProvider provider, final String meetingId) {
        switch (provider) {
            case ZOOM -> zoomMeetingLinkProcessor.delete(meetingId);
            case GOOGLE -> throw new UnsupportedOperationException("not supported yet...");
        }
    }
}
