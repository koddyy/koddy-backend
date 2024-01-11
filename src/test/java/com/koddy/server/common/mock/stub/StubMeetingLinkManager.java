package com.koddy.server.common.mock.stub;

import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubMeetingLinkManager implements MeetingLinkManager {
    @Override
    public boolean isSupported(final MeetingLinkProvider provider) {
        return true;
    }

    @Override
    public ZoomMeetingLinkResponse create(final String accessToken, final ZoomMeetingLinkRequest meetingLinkRequest) {
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
    public void delete(final String meetingId) {
        log.info("Meeting [{}] 삭제", meetingId);
    }
}
