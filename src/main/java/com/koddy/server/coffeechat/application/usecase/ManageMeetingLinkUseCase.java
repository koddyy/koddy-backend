package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.coffeechat.application.usecase.command.CreateMeetingLinkCommand;
import com.koddy.server.coffeechat.application.usecase.command.DeleteMeetingLinkCommand;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkRequest;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.redis.RedisOperator;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_OAUTH_PROVIDER;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER;

@UseCase
@RequiredArgsConstructor
public class ManageMeetingLinkUseCase {
    /**
     * Key = User Platform ID <br>
     * Value = User OAuth AccessToken
     */
    private static final String USER_OAUTH_TOKEN_FROM_PLATFORM_TOKEN = "USER_OAUTH_TOKEN:PLATFORM:%s";

    private final List<OAuthConnector> oAuthConnectors;
    private final List<MeetingLinkManager> meetingLinkManagers;
    private final RedisOperator<String, String> redisOperator;

    /**
     * 추후 Google Meet Creator 연동하면서 MeetingLinkCreator Request/Response 스펙 재정의 (Interface)
     */
    public ZoomMeetingLinkResponse create(final CreateMeetingLinkCommand command) {
        final String oAuthAccessToken = getOAuthAccessToken(command);
        final MeetingLinkManager meetingLinkManager = getMeetingLinkCreatorByProvider(command.linkProvider());
        return meetingLinkManager.create(oAuthAccessToken, createRequest(command));
    }

    private String getOAuthAccessToken(final CreateMeetingLinkCommand command) {
        final String cacheKey = createCacheKey(String.valueOf(command.memberId()));
        if (redisOperator.contains(cacheKey)) {
            return redisOperator.get(cacheKey);
        }

        final String oAuthAccessToken = getOAuthConnectorByProvider(command.oAuthProvider())
                .fetchToken(command.code(), command.redirectUri(), command.state())
                .accessToken();
        redisOperator.save(cacheKey, oAuthAccessToken, Duration.ofMinutes(10));
        return oAuthAccessToken;
    }

    private String createCacheKey(final String suffix) {
        return String.format(USER_OAUTH_TOKEN_FROM_PLATFORM_TOKEN, suffix);
    }

    private OAuthConnector getOAuthConnectorByProvider(final OAuthProvider provider) {
        return oAuthConnectors.stream()
                .filter(oAuthConnector -> oAuthConnector.isSupported(provider))
                .findFirst()
                .orElseThrow(() -> new AuthException(INVALID_OAUTH_PROVIDER));
    }

    private MeetingLinkManager getMeetingLinkCreatorByProvider(final MeetingLinkProvider provider) {
        return meetingLinkManagers.stream()
                .filter(meetingLinkManager -> meetingLinkManager.isSupported(provider))
                .findFirst()
                .orElseThrow(() -> new CoffeeChatException(INVALID_MEETING_LINK_PROVIDER));
    }

    private ZoomMeetingLinkRequest createRequest(final CreateMeetingLinkCommand command) {
        return new ZoomMeetingLinkRequest(command.topic(), command.start(), command.end());
    }

    public void delete(final DeleteMeetingLinkCommand command) {
        final MeetingLinkManager meetingLinkManager = getMeetingLinkCreatorByProvider(command.linkProvider());
        meetingLinkManager.delete(command.meetingId());
    }
}
