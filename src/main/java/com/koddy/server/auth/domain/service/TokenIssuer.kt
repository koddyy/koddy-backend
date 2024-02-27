package com.koddy.server.auth.domain.service;

import com.koddy.server.auth.application.adapter.TokenStore;
import com.koddy.server.auth.domain.model.AuthToken;
import org.springframework.stereotype.Service;

@Service
public class TokenIssuer {
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;

    public TokenIssuer(
            final TokenProvider tokenProvider,
            final TokenStore tokenStore
    ) {
        this.tokenProvider = tokenProvider;
        this.tokenStore = tokenStore;
    }

    public AuthToken provideAuthorityToken(final long memberId, final String authority) {
        final String accessToken = tokenProvider.createAccessToken(memberId, authority);
        final String refreshToken = tokenProvider.createRefreshToken(memberId);
        tokenStore.synchronizeRefreshToken(memberId, refreshToken);

        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken reissueAuthorityToken(final long memberId, final String authority) {
        final String newAccessToken = tokenProvider.createAccessToken(memberId, authority);
        final String newRefreshToken = tokenProvider.createRefreshToken(memberId);
        tokenStore.updateRefreshToken(memberId, newRefreshToken);

        return new AuthToken(newAccessToken, newRefreshToken);
    }

    public boolean isMemberRefreshToken(final long memberId, final String refreshToken) {
        return tokenStore.isMemberRefreshToken(memberId, refreshToken);
    }

    public void deleteRefreshToken(final long memberId) {
        tokenStore.deleteRefreshToken(memberId);
    }
}
