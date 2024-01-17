package com.koddy.server.common.mock.stub;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.common.fixture.OAuthFixture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubMeetingLinkManager implements MeetingLinkManager {
    @Override
    public OAuthTokenResponse fetchToken(
            final OAuthProvider provider,
            final String code,
            final String redirectUri,
            final String state
    ) {
        return OAuthFixture.parseOAuthTokenByCode(code);
    }

    @Override
    public MeetingLinkResponse create(
            final MeetingLinkProvider provider,
            final String accessToken,
            final MeetingLinkRequest meetingLinkRequest
    ) {
        log.info("Meeting 생성");
        return new ZoomMeetingLinkResponse(
                "zoom-meeting-id",
                "sjiwon4491@gmail.com",
                "Hello 줌 회의",
                "zoom-join-url",
                60
        );
    }

    @Override
    public void delete(final MeetingLinkProvider provider, final String meetingId) {
        log.info("Meeting [{}] 삭제", meetingId);
    }
}
