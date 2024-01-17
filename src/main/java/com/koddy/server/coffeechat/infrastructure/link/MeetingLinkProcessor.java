package com.koddy.server.coffeechat.infrastructure.link;

import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;

public interface MeetingLinkProcessor {
    MeetingLinkResponse create(final String oAuthAccessToken, final MeetingLinkRequest meetingLinkRequest);

    void delete(final String meetingId);

    String OAUTH_CONTENT_TYPE = "application/json";

    String BEARER_TOKEN_TYPE = "Bearer";
}
