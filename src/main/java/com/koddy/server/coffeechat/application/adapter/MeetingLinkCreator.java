package com.koddy.server.coffeechat.application.adapter;

import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;

public interface MeetingLinkCreator {
    boolean isSupported(final MeetingLinkProvider provider);

    ZoomMeetingLinkResponse create(final String accessToken, final ZoomMeetingLinkRequest meetingLinkRequest);

    void delete(final String accessToken, final String meetingId);

    String LINK_REQUEST_CONTENT_TYPE = "application/json";

    String BEARER_TOKEN_TYPE = "Bearer";
}
