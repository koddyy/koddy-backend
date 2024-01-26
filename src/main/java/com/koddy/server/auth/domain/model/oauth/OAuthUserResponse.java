package com.koddy.server.auth.domain.model.oauth;

public interface OAuthUserResponse {
    String id();

    String name();

    String email();

    String profileImageUrl();
}
