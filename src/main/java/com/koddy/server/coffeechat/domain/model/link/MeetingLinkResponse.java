package com.koddy.server.coffeechat.domain.model.link;

public interface MeetingLinkResponse {
    String id();

    String hostEmail();

    String topic();

    String joinUrl();

    long duration();
}
