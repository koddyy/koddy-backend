package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkTokenCashier;
import com.koddy.server.coffeechat.application.usecase.command.CreateMeetingLinkCommand;
import com.koddy.server.coffeechat.application.usecase.command.DeleteMeetingLinkCommand;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.global.annotation.UseCase;

import java.time.Duration;

@UseCase
public class ManageMeetingLinkUseCase {
    private final MeetingLinkManager meetingLinkManager;
    private final MeetingLinkTokenCashier meetingLinkTokenCashier;

    public ManageMeetingLinkUseCase(
            final MeetingLinkManager meetingLinkManager,
            final MeetingLinkTokenCashier meetingLinkTokenCashier
    ) {
        this.meetingLinkManager = meetingLinkManager;
        this.meetingLinkTokenCashier = meetingLinkTokenCashier;
    }

    /**
     * 추후 Google Meet Creator 연동하면서 MeetingLinkCreator Request/Response 스펙 재정의 (Interface)
     */
    public MeetingLinkResponse create(final CreateMeetingLinkCommand command) {
        final String oAuthAccessToken = getOAuthAccessToken(command);
        return meetingLinkManager.create(command.linkProvider(), oAuthAccessToken, createRequest(command));
    }

    private String getOAuthAccessToken(final CreateMeetingLinkCommand command) {
        if (meetingLinkTokenCashier.containsViaPlatformId(command.memberId())) {
            return meetingLinkTokenCashier.getViaPlatformId(command.memberId());
        }

        final OAuthTokenResponse token = meetingLinkManager.fetchToken(
                command.oAuthProvider(),
                command.code(),
                command.redirectUri(),
                command.state()
        );
        meetingLinkTokenCashier.storeViaPlatformId(command.memberId(), token.accessToken(), Duration.ofMinutes(10));
        return token.accessToken();
    }

    private ZoomMeetingLinkRequest createRequest(final CreateMeetingLinkCommand command) {
        return new ZoomMeetingLinkRequest(command.topic(), command.start(), command.end());
    }

    public void delete(final DeleteMeetingLinkCommand command) {
        meetingLinkManager.delete(command.linkProvider(), command.meetingId());
    }
}
