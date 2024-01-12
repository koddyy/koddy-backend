package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor;
import com.koddy.server.auth.application.usecase.command.OAuthLoginCommand;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.auth.exception.OAuthUserNotFoundException;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.koddy.server.common.UseCaseTest;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.OAuthUtils.AUTHORIZATION_CODE;
import static com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI;
import static com.koddy.server.common.utils.OAuthUtils.STATE;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.createGoogleTokenResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> OAuthLoginUseCase 테스트")
class OAuthLoginUseCaseTest extends UseCaseTest {
    private final OAuthLoginProcessor oAuthLoginProcessor = mock(OAuthLoginProcessor.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final TokenIssuer tokenIssuer = mock(TokenIssuer.class);
    private final OAuthLoginUseCase sut = new OAuthLoginUseCase(oAuthLoginProcessor, memberRepository, tokenIssuer);

    @Nested
    @DisplayName("Google OAuth 로그인")
    class GoogleLogin {
        private final Member<?> member = MENTOR_1.toDomain().apply(1L);
        private final OAuthLoginCommand command = new OAuthLoginCommand(GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE);
        private final GoogleTokenResponse googleTokenResponse = createGoogleTokenResponse();
        private final GoogleUserResponse googleUserResponse = MENTOR_1.toGoogleUserResponse();

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하지 않으면 예외를 발생하고 회원가입을 진행한다")
        void throwExceptionIfGoogleAuthUserNotInDB() {
            // given
            given(oAuthLoginProcessor.login(GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE)).willReturn(googleUserResponse);
            given(memberRepository.findByEmailValue(googleUserResponse.email())).willReturn(Optional.empty());

            // when - then
            final OAuthUserNotFoundException exception = assertThrows(
                    OAuthUserNotFoundException.class,
                    () -> sut.invoke(command)
            );

            assertAll(
                    () -> verify(oAuthLoginProcessor, times(1)).login(GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE),
                    () -> verify(memberRepository, times(1)).findByEmailValue(googleUserResponse.email()),
                    () -> verify(tokenIssuer, times(0)).provideAuthorityToken(member.getId()),
                    () -> assertThat(exception.getResponse())
                            .usingRecursiveComparison()
                            .isEqualTo(googleUserResponse)
            );
        }

        @Test
        @DisplayName("Google OAuth 로그인을 진행할 때 해당 사용자가 DB에 존재하면 로그인에 성공하고 사용자 정보 및 토큰을 발급해준다")
        void success() {
            // given
            given(oAuthLoginProcessor.login(GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE)).willReturn(googleUserResponse);
            given(memberRepository.findByEmailValue(googleUserResponse.email())).willReturn(Optional.of(member));

            final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
            given(tokenIssuer.provideAuthorityToken(member.getId())).willReturn(authToken);

            // when
            final AuthMember response = sut.invoke(command);

            // then
            assertAll(
                    () -> verify(oAuthLoginProcessor, times(1)).login(GOOGLE, AUTHORIZATION_CODE, REDIRECT_URI, STATE),
                    () -> verify(memberRepository, times(1)).findByEmailValue(googleUserResponse.email()),
                    () -> verify(tokenIssuer, times(1)).provideAuthorityToken(member.getId()),
                    () -> assertThat(response.id()).isEqualTo(member.getId()),
                    () -> assertThat(response.name()).isEqualTo(member.getName()),
                    () -> assertThat(response.profileImageUrl()).isEqualTo(member.getProfileImageUrl()),
                    () -> assertThat(response.token().accessToken()).isEqualTo(authToken.accessToken()),
                    () -> assertThat(response.token().refreshToken()).isEqualTo(authToken.refreshToken())
            );
        }
    }
}
