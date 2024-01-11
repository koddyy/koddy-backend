package com.koddy.server.auth.domain.service;

import com.koddy.server.auth.domain.model.Token;
import com.koddy.server.auth.domain.repository.TokenRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;

    @KoddyWritableTransactional
    public void synchronizeRefreshToken(final long memberId, final String refreshToken) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(new Token(memberId, refreshToken))
                );
    }

    public void updateRefreshToken(final long memberId, final String newRefreshToken) {
        tokenRepository.updateRefreshToken(memberId, newRefreshToken);
    }

    public void deleteRefreshToken(final long memberId) {
        tokenRepository.deleteRefreshToken(memberId);
    }

    public boolean isMemberRefreshToken(final long memberId, final String refreshToken) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}
