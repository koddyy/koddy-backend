package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.common.UseCaseTest;
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand;
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> SignUpUsecase 테스트")
class SignUpUsecaseTest extends UseCaseTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final MenteeRepository menteeRepository = mock(MenteeRepository.class);
    private final TokenIssuer tokenIssuer = mock(TokenIssuer.class);
    private final SignUpUsecase sut = new SignUpUsecase(
            mentorRepository,
            menteeRepository,
            tokenIssuer
    );

    @Test
    @DisplayName("멘토 회원가입 후 바로 로그인 처리를 진행한다")
    void signUpMentor() {
        // given
        final SignUpMentorCommand command = new SignUpMentorCommand(
                MENTOR_1.getEmail(),
                MENTOR_1.getName(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile()
        );

        final Mentor mentor = MENTOR_1.toDomain().apply(1L);
        given(mentorRepository.save(any(Mentor.class))).willReturn(mentor);

        final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
        given(tokenIssuer.provideAuthorityToken(mentor.getId())).willReturn(authToken);

        // when
        final AuthMember authMember = sut.signUpMentor(command);

        // then
        assertAll(
                () -> verify(mentorRepository, times(1)).save(any(Mentor.class)),
                () -> verify(tokenIssuer, times(1)).provideAuthorityToken(mentor.getId()),
                () -> assertThat(authMember.id()).isEqualTo(mentor.getId()),
                () -> assertThat(authMember.name()).isEqualTo(mentor.getName()),
                () -> assertThat(authMember.profileImageUrl()).isEqualTo(mentor.getProfileImageUrl()),
                () -> assertThat(authMember.token().accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authMember.token().refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }

    @Test
    @DisplayName("멘티 회원가입 후 바로 로그인 처리를 진행한다")
    void signUpMentee() {
        // given
        final SignUpMenteeCommand command = new SignUpMenteeCommand(
                MENTEE_1.getEmail(),
                MENTEE_1.getName(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getNationality(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest()
        );

        final Mentee mentee = MENTEE_1.toDomain().apply(1L);
        given(menteeRepository.save(any(Mentee.class))).willReturn(mentee);

        final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
        given(tokenIssuer.provideAuthorityToken(mentee.getId())).willReturn(authToken);

        // when
        final AuthMember authMember = sut.signUpMentee(command);

        // then
        assertAll(
                () -> verify(menteeRepository, times(1)).save(any(Mentee.class)),
                () -> verify(tokenIssuer, times(1)).provideAuthorityToken(mentee.getId()),
                () -> assertThat(authMember.id()).isEqualTo(mentee.getId()),
                () -> assertThat(authMember.name()).isEqualTo(mentee.getName()),
                () -> assertThat(authMember.profileImageUrl()).isEqualTo(mentee.getProfileImageUrl()),
                () -> assertThat(authMember.token().accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authMember.token().refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }
}
