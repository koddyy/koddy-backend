package com.koddy.server.coffeechat.infrastructure.store;

import com.koddy.server.coffeechat.application.adapter.MeetingLinkTokenCashier;
import com.koddy.server.global.utils.redis.RedisOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisMeetingLinkTokenCashier implements MeetingLinkTokenCashier {
    /**
     * Key = User Platform ID <br>
     * Value = User OAuth AccessToken
     */
    private static final String USER_OAUTH_TOKEN_FROM_PLATFORM_ID = "USER_OAUTH_TOKEN:PLATFORM:%s";

    /**
     * Key = User Created Meeting ID <br>
     * Value = User OAuth AccessToken
     */
    private static final String USER_OAUTH_TOKEN_FROM_MEETING_ID = "USER_OAUTH_TOKEN:CREATE_MEETING:%s";

    private final RedisOperator<String, String> redisOperator;

    @Override
    public void storeViaPlatformId(final long platformId, final String oAuthAccessToken, final Duration duration) {
        redisOperator.save(createKeyViaPlatformId(platformId), oAuthAccessToken, duration);
    }

    @Override
    public String getViaPlatformId(final long platformId) {
        return redisOperator.get(createKeyViaPlatformId(platformId));
    }

    @Override
    public boolean containsViaPlatformId(final long platformId) {
        return redisOperator.contains(createKeyViaPlatformId(platformId));
    }

    private String createKeyViaPlatformId(final long platformId) {
        return String.format(USER_OAUTH_TOKEN_FROM_PLATFORM_ID, platformId);
    }

    @Override
    public void storeViaMeetingId(final String meetingId, final String oAuthAccessToken, final Duration duration) {
        redisOperator.save(createKeyViaMeetingId(meetingId), oAuthAccessToken, duration);
    }

    @Override
    public String getViaMeetingId(final String meetingId) {
        return redisOperator.get(createKeyViaMeetingId(meetingId));
    }

    @Override
    public boolean containsViaMeetingId(final String meetingId) {
        return redisOperator.contains(createKeyViaMeetingId(meetingId));
    }

    @Override
    public void deleteViaMeetingId(final String meetingId) {
        redisOperator.delete(createKeyViaMeetingId(meetingId));
    }

    private String createKeyViaMeetingId(final String meetingId) {
        return String.format(USER_OAUTH_TOKEN_FROM_MEETING_ID, meetingId);
    }
}
