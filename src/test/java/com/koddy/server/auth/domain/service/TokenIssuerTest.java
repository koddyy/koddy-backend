package com.koddy.server.auth.domain.service;

import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.utils.TokenProvider;
import com.koddy.server.common.ParallelTest;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> TokenIssuer 테스트")
public class TokenIssuerTest extends ParallelTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final TokenManager tokenManager = mock(TokenManager.class);
    private final TokenIssuer sut = new TokenIssuer(memberRepository, tokenProvider, tokenManager);

    private final Member<?> member = MENTOR_1.toDomain().apply(1L);

    @Test
    @DisplayName("AuthToken[Access + Refresh]을 제공한다")
    void provideAuthorityToken() {
        // given
        given(memberRepository.getByIdWithRoles(member.getId())).willReturn(member);
        given(tokenProvider.createAccessToken(member.getId(), member.getRoleTypes())).willReturn(ACCESS_TOKEN);
        given(tokenProvider.createRefreshToken(member.getId())).willReturn(REFRESH_TOKEN);

        // when
        final AuthToken authToken = sut.provideAuthorityToken(member.getId());

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).getByIdWithRoles(member.getId()),
                () -> verify(tokenProvider, times(1)).createAccessToken(member.getId(), member.getRoleTypes()),
                () -> verify(tokenProvider, times(1)).createRefreshToken(member.getId()),
                () -> verify(tokenManager, times(1)).synchronizeRefreshToken(member.getId(), REFRESH_TOKEN),
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }

    @Test
    @DisplayName("AuthToken[Access + Refresh]을 재발급한다")
    void reissueAuthorityToken() {
        // given
        given(memberRepository.getByIdWithRoles(member.getId())).willReturn(member);
        given(tokenProvider.createAccessToken(member.getId(), member.getRoleTypes())).willReturn(ACCESS_TOKEN);
        given(tokenProvider.createRefreshToken(member.getId())).willReturn(REFRESH_TOKEN);

        // when
        final AuthToken authToken = sut.reissueAuthorityToken(member.getId());

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).getByIdWithRoles(member.getId()),
                () -> verify(tokenProvider, times(1)).createAccessToken(member.getId(), member.getRoleTypes()),
                () -> verify(tokenProvider, times(1)).createRefreshToken(member.getId()),
                () -> verify(tokenManager, times(1)).updateRefreshToken(member.getId(), REFRESH_TOKEN),
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }
}
