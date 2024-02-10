package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.common.UnitTest;
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand;
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.koddy.server.member.exception.MemberExceptionCode.ACCOUNT_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> SignUpUsecase 테스트")
class SignUpUsecaseTest extends UnitTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final TokenIssuer tokenIssuer = mock(TokenIssuer.class);
    private final SignUpUsecase sut = new SignUpUsecase(memberRepository, tokenIssuer);

    @Nested
    @DisplayName("멘토 회원가입 + 로그인")
    class SignUpMentor {
        private final SignUpMentorCommand command = new SignUpMentorCommand(
                MENTOR_1.getPlatform(),
                MENTOR_1.getName(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile()
        );

        @Test
        @DisplayName("이메일에 해당하는 계정이 있으면 회원가입이 불가능하다")
        void throwExceptionByAccountAlreadyExists() {
            // given
            given(memberRepository.existsByPlatformSocialId(command.platform().getSocialId())).willReturn(true);

            // when - then
            assertAll(
                    () -> assertThatThrownBy(() -> sut.signUpMentor(command))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(ACCOUNT_ALREADY_EXISTS.getMessage()),
                    () -> verify(memberRepository, times(1)).existsByPlatformSocialId(command.platform().getSocialId()),
                    () -> verify(memberRepository, times(0)).save(any(Mentor.class)),
                    () -> verify(tokenIssuer, times(0)).provideAuthorityToken(anyLong(), anyString())
            );
        }

        @Test
        @DisplayName("멘토 회원가입 후 바로 로그인 처리를 진행한다")
        void success() {
            // given
            given(memberRepository.existsByPlatformSocialId(command.platform().getSocialId())).willReturn(false);

            final Mentor mentor = MENTOR_1.toDomain().apply(1L);
            given(memberRepository.save(any(Mentor.class))).willReturn(mentor);

            final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
            given(tokenIssuer.provideAuthorityToken(mentor.getId(), mentor.getAuthority())).willReturn(authToken);

            // when
            final AuthMember authMember = sut.signUpMentor(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).existsByPlatformSocialId(command.platform().getSocialId()),
                    () -> verify(memberRepository, times(1)).save(any(Mentor.class)),
                    () -> verify(tokenIssuer, times(1)).provideAuthorityToken(mentor.getId(), mentor.getAuthority()),
                    () -> assertThat(authMember.id()).isEqualTo(mentor.getId()),
                    () -> assertThat(authMember.name()).isEqualTo(mentor.getName()),
                    () -> assertThat(authMember.profileImageUrl()).isEqualTo(mentor.getProfileImageUrl()),
                    () -> assertThat(authMember.token().accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(authMember.token().refreshToken()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }

    @Nested
    @DisplayName("멘티 회원가입 + 로그인")
    class SignUpMentee {
        private final SignUpMenteeCommand command = new SignUpMenteeCommand(
                MENTEE_1.getPlatform(),
                MENTEE_1.getName(),
                MENTEE_1.getNationality(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest()
        );

        @Test
        @DisplayName("이메일에 해당하는 계정이 있으면 회원가입이 불가능하다")
        void throwExceptionByAccountAlreadyExists() {
            // given
            given(memberRepository.existsByPlatformSocialId(command.platform().getSocialId())).willReturn(true);

            // when - then
            assertAll(
                    () -> assertThatThrownBy(() -> sut.signUpMentee(command))
                            .isInstanceOf(MemberException.class)
                            .hasMessage(ACCOUNT_ALREADY_EXISTS.getMessage()),
                    () -> verify(memberRepository, times(1)).existsByPlatformSocialId(command.platform().getSocialId()),
                    () -> verify(memberRepository, times(0)).save(any(Mentee.class)),
                    () -> verify(tokenIssuer, times(0)).provideAuthorityToken(anyLong(), anyString())
            );
        }

        @Test
        @DisplayName("멘티 회원가입 후 바로 로그인 처리를 진행한다")
        void success() {
            // given
            given(memberRepository.existsByPlatformSocialId(command.platform().getSocialId())).willReturn(false);

            final Mentee mentee = MENTEE_1.toDomain().apply(1L);
            given(memberRepository.save(any(Mentee.class))).willReturn(mentee);

            final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
            given(tokenIssuer.provideAuthorityToken(mentee.getId(), mentee.getAuthority())).willReturn(authToken);

            // when
            final AuthMember authMember = sut.signUpMentee(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).existsByPlatformSocialId(command.platform().getSocialId()),
                    () -> verify(memberRepository, times(1)).save(any(Mentee.class)),
                    () -> verify(tokenIssuer, times(1)).provideAuthorityToken(mentee.getId(), mentee.getAuthority()),
                    () -> assertThat(authMember.id()).isEqualTo(mentee.getId()),
                    () -> assertThat(authMember.name()).isEqualTo(mentee.getName()),
                    () -> assertThat(authMember.profileImageUrl()).isEqualTo(mentee.getProfileImageUrl()),
                    () -> assertThat(authMember.token().accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(authMember.token().refreshToken()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }
}
