package com.koddy.server.coffeechat.application.adapter;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;

public interface MeetingLinkManager {
    OAuthTokenResponse fetchToken(
            final OAuthProvider provider,
            final String code,
            final String redirectUri,
            final String state
    );

    MeetingLinkResponse create(
            final MeetingLinkProvider provider,
            final String accessToken,
            final ZoomMeetingLinkRequest meetingLinkRequest
    );

    void delete(final MeetingLinkProvider provider, final String meetingId);
}
