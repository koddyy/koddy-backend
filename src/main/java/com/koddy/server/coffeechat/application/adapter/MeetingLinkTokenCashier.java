package com.koddy.server.coffeechat.application.adapter;

import java.time.Duration;

public interface MeetingLinkTokenCashier {
    void storeViaPlatformId(final long platformId, final String oAuthAccessToken, final Duration duration);

    String getViaPlatformId(final long platformId);

    boolean containsViaPlatformId(final long platformId);

    void storeViaMeetingId(final String meetingId, final String oAuthAccessToken, final Duration duration);

    String getViaMeetingId(final String meetingId);

    boolean containsViaMeetingId(final String meetingId);

    void deleteViaMeetingId(final String meetingId);
}
