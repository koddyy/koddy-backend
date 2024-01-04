package com.koddy.server.coffeechat.application.adapter;

public interface MeetingLinkCreator {
    boolean isSupported(final LinkType linkType);

    String create();

    enum LinkType {
        ZOOM, GOOGLE_MEET
    }
}
