package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.application.usecase.command.CreateMeetingLinkCommand;
import com.koddy.server.coffeechat.application.usecase.command.DeleteMeetingLinkCommand;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.redis.RedisOperator;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@UseCase
@RequiredArgsConstructor
public class ManageMeetingLinkUseCase {
    /**
     * Key = User Platform ID <br>
     * Value = User OAuth AccessToken
     */
    private static final String USER_OAUTH_TOKEN_FROM_PLATFORM_TOKEN = "USER_OAUTH_TOKEN:PLATFORM:%s";

    private final MeetingLinkManager meetingLinkManager;
    private final RedisOperator<String, String> redisOperator;

    /**
     * 추후 Google Meet Creator 연동하면서 MeetingLinkCreator Request/Response 스펙 재정의 (Interface)
     */
    public MeetingLinkResponse create(final CreateMeetingLinkCommand command) {
        final String oAuthAccessToken = getOAuthAccessToken(command);
        return meetingLinkManager.create(command.linkProvider(), oAuthAccessToken, createRequest(command));
    }

    private String getOAuthAccessToken(final CreateMeetingLinkCommand command) {
        final String cacheKey = createCacheKey(String.valueOf(command.memberId()));
        if (redisOperator.contains(cacheKey)) {
            return redisOperator.get(cacheKey);
        }

        final OAuthTokenResponse token = meetingLinkManager.fetchToken(
                command.oAuthProvider(),
                command.code(),
                command.redirectUri(),
                command.state()
        );
        redisOperator.save(cacheKey, token.accessToken(), Duration.ofMinutes(10));
        return token.accessToken();
    }

    private String createCacheKey(final String suffix) {
        return String.format(USER_OAUTH_TOKEN_FROM_PLATFORM_TOKEN, suffix);
    }

    private ZoomMeetingLinkRequest createRequest(final CreateMeetingLinkCommand command) {
        return new ZoomMeetingLinkRequest(command.topic(), command.start(), command.end());
    }

    public void delete(final DeleteMeetingLinkCommand command) {
        meetingLinkManager.delete(command.linkProvider(), command.meetingId());
    }
}
