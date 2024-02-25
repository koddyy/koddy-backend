package com.koddy.server.auth.infrastructure.token;

import com.koddy.server.auth.application.adapter.TokenStore;
import com.koddy.server.auth.domain.model.Token;
import com.koddy.server.auth.domain.repository.TokenRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import org.springframework.stereotype.Component;

@Component
public class RdbTokenStore implements TokenStore {
    private final TokenRepository tokenRepository;

    public RdbTokenStore(final TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @KoddyWritableTransactional
    @Override
    public void synchronizeRefreshToken(final Long memberId, final String refreshToken) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(new Token(memberId, refreshToken))
                );
    }

    @Override
    public void updateRefreshToken(final long memberId, final String newRefreshToken) {
        tokenRepository.updateRefreshToken(memberId, newRefreshToken);
    }

    @Override
    public void deleteRefreshToken(final long memberId) {
        tokenRepository.deleteRefreshToken(memberId);
    }

    @Override
    public boolean isMemberRefreshToken(final long memberId, final String refreshToken) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}
