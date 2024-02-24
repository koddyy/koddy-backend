package com.koddy.server.member.domain.model;

import com.koddy.server.auth.domain.model.oauth.OAuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;

import static jakarta.persistence.EnumType.STRING;

@Embeddable
public class SocialPlatform {
    protected SocialPlatform() {
    }

    @Enumerated(STRING)
    @Column(name = "social_provider", columnDefinition = "VARCHAR(30)")
    private OAuthProvider provider;

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Embedded
    private Email email;

    public SocialPlatform(final OAuthProvider provider, final String socialId, final Email email) {
        this.provider = provider;
        this.socialId = socialId;
        this.email = email;
    }

    public SocialPlatform syncEmail(final Email email) {
        return new SocialPlatform(provider, socialId, email);
    }

    public OAuthProvider getProvider() {
        return provider;
    }

    public String getSocialId() {
        return socialId;
    }

    public Email getEmail() {
        return email;
    }
}
