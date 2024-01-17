package com.koddy.server.auth.domain.service;

import com.koddy.server.auth.application.adapter.TokenStore;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.utils.TokenProvider;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenIssuer {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;

    public AuthToken provideAuthorityToken(final long memberId) {
        final Member<?> member = memberRepository.getById(memberId);
        final String accessToken = tokenProvider.createAccessToken(memberId, member.getAuthority());
        final String refreshToken = tokenProvider.createRefreshToken(memberId);
        tokenStore.synchronizeRefreshToken(memberId, refreshToken);

        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken reissueAuthorityToken(final long memberId) {
        final Member<?> member = memberRepository.getById(memberId);
        final String newAccessToken = tokenProvider.createAccessToken(memberId, member.getAuthority());
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
