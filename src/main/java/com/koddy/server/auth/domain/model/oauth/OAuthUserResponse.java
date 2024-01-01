package com.koddy.server.auth.domain.model.oauth;

public interface OAuthUserResponse {
    String socialId();

    String name();

    String email();

    String profileImageUrl();
}
