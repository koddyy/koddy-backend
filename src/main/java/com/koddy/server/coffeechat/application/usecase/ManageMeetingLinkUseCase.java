package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.application.usecase.command.CreateMeetingLinkCommand;
import com.koddy.server.coffeechat.application.usecase.command.DeleteMeetingLinkCommand;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER;

@UseCase
@RequiredArgsConstructor
public class ManageMeetingLinkUseCase {
    private final List<OAuthConnector> oAuthConnectors;
    private final List<MeetingLinkManager> meetingLinkManagers;

    /**
     * 추후 Google Meet Creator 연동하면서 MeetingLinkCreator Request/Response 스펙 재정의 (Interface)
     */
    public ZoomMeetingLinkResponse create(final CreateMeetingLinkCommand command) {
        final OAuthTokenResponse oAuthToken = getOAuthToken(command);
        final MeetingLinkManager meetingLinkManager = getMeetingLinkCreatorByProvider(command.linkProvider());
        return meetingLinkManager.create(oAuthToken.accessToken(), createRequest(command));
    }

    private OAuthTokenResponse getOAuthToken(final CreateMeetingLinkCommand command) {
        final OAuthConnector oAuthConnector = getOAuthConnectorByProvider(command.oAuthProvider());
        return oAuthConnector.fetchToken(command.code(), command.redirectUri(), command.state());
    }

    private OAuthConnector getOAuthConnectorByProvider(final OAuthProvider provider) {
        return oAuthConnectors.stream()
                .filter(oAuthConnector -> oAuthConnector.isSupported(provider))
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_OAUTH_PROVIDER));
    }

    private MeetingLinkManager getMeetingLinkCreatorByProvider(final MeetingLinkProvider provider) {
        return meetingLinkManagers.stream()
                .filter(meetingLinkManager -> meetingLinkManager.isSupported(provider))
                .findFirst()
                .orElseThrow(() -> new CoffeeChatException(INVALID_MEETING_LINK_PROVIDER));
    }

    private ZoomMeetingLinkRequest createRequest(final CreateMeetingLinkCommand command) {
        return new ZoomMeetingLinkRequest(command.topic(), command.start(), command.end());
    }

    public void delete(final DeleteMeetingLinkCommand command) {
        final MeetingLinkManager meetingLinkManager = getMeetingLinkCreatorByProvider(command.linkProvider());
        meetingLinkManager.delete(command.meetingId());
    }
}
