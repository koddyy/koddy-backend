package com.koddy.server.auth.application.usecase.command;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;

public record OAuthLoginCommand(
        OAuthProvider provider,
        String code,
        String redirectUrl,
        String state
) {
}
