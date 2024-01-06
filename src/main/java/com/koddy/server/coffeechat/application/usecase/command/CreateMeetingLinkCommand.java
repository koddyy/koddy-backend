package com.koddy.server.coffeechat.application.usecase.command;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;

import java.time.LocalDateTime;

public record CreateMeetingLinkCommand(
        Long memberId,
        OAuthProvider oAuthProvider,
        MeetingLinkProvider linkProvider,
        String code,
        String redirectUri,
        String state,
        String topic,
        LocalDateTime start,
        LocalDateTime end
) {
}
