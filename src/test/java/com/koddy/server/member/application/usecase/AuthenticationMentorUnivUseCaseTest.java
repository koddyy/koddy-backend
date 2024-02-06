package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.application.adapter.AuthenticationProcessor;
import com.koddy.server.auth.domain.model.code.AuthKeyGenerator;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.UnitTest;
import com.koddy.server.member.application.usecase.command.AuthenticationConfirmWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithProofDataCommand;
import com.koddy.server.member.domain.event.MailAuthenticatedEvent;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.ATTEMPT;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> AuthenticationMentorUnivUseCase 테스트")
class AuthenticationMentorUnivUseCaseTest extends UnitTest {
    private static final String AUTH_KEY_PREFIX = "MENTOR-MAIL-AUTH:%d:%s";
    private static final String AUTH_CODE = "123456";

    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final AuthKeyGenerator authKeyGenerator = (prefix, suffix) -> String.format(AUTH_KEY_PREFIX, suffix);
    private final AuthenticationProcessor authenticationProcessor = mock(AuthenticationProcessor.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final AuthenticationMentorUnivUseCase sut = new AuthenticationMentorUnivUseCase(
            mentorRepository,
            authKeyGenerator,
            authenticationProcessor,
            eventPublisher
    );

    @Nested
    @DisplayName("학교 메일 인증 시도")
    class AuthWithMail {
        private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
        private final String schoolMail = "sjiwon@kyonggi.ac.kr";
        private final AuthenticationWithMailCommand command = new AuthenticationWithMailCommand(mentor.getId(), schoolMail);

        @Test
        @DisplayName("학교 메일로 멘토 인증을 시도한다")
        void success() {
            // given
            given(mentorRepository.getById(command.mentorId())).willReturn(mentor);

            // when
            sut.authWithMail(command);

            // then
            assertAll(
                    () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                    () -> verify(authenticationProcessor, times(1)).storeAuthCode(getAuthKey(mentor.getId(), schoolMail)),
                    () -> verify(eventPublisher, times(1)).publishEvent(any(MailAuthenticatedEvent.class)),
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT)
            );
        }
    }

    @Nested
    @DisplayName("학교 메일 인증 확인")
    class ConfirmMailAuthCode {
        @Test
        @DisplayName("인증번호가 일치하지 않음에 따라 인증에 실패한다")
        void throwExceptionByInvalidAuthCode() {
            // given
            final Mentor mentor = MENTOR_1.toDomain().apply(1L);
            final String schoolMail = "sjiwon@kyonggi.ac.kr";
            mentor.authWithMail(schoolMail);

            final AuthenticationConfirmWithMailCommand command = new AuthenticationConfirmWithMailCommand(
                    mentor.getId(),
                    schoolMail,
                    AUTH_CODE + "diff"
            );

            given(mentorRepository.getById(command.mentorId())).willReturn(mentor);
            doThrow(new AuthException(INVALID_AUTH_CODE))
                    .when(authenticationProcessor)
                    .verifyAuthCode(getAuthKey(mentor.getId(), schoolMail), command.authCode());

            // when - then
            assertThatThrownBy(() -> sut.confirmMailAuthCode(command))
                    .isInstanceOf(AuthException.class)
                    .hasMessage(INVALID_AUTH_CODE.getMessage());

            assertAll(
                    () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                    () -> verify(authenticationProcessor, times(1)).verifyAuthCode(getAuthKey(mentor.getId(), schoolMail), command.authCode()),
                    () -> verify(authenticationProcessor, times(0)).deleteAuthCode(getAuthKey(mentor.getId(), schoolMail)),
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT)
            );
        }

        @Test
        @DisplayName("인증번호가 일치하고 그에 따라서 학교 메일 인증에 성공한다")
        void success() {
            // given
            final Mentor mentor = MENTOR_1.toDomain().apply(1L);
            final String schoolMail = "sjiwon@kyonggi.ac.kr";
            mentor.authWithMail(schoolMail);

            final AuthenticationConfirmWithMailCommand command = new AuthenticationConfirmWithMailCommand(
                    mentor.getId(),
                    schoolMail,
                    AUTH_CODE
            );

            given(mentorRepository.getById(command.mentorId())).willReturn(mentor);
            doNothing()
                    .when(authenticationProcessor)
                    .verifyAuthCode(getAuthKey(mentor.getId(), schoolMail), command.authCode());

            // when
            sut.confirmMailAuthCode(command);

            // then
            assertAll(
                    () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                    () -> verify(authenticationProcessor, times(1)).verifyAuthCode(getAuthKey(mentor.getId(), schoolMail), command.authCode()),
                    () -> verify(authenticationProcessor, times(1)).deleteAuthCode(getAuthKey(mentor.getId(), schoolMail)),
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(SUCCESS)
            );
        }
    }

    @Nested
    @DisplayName("학교 증명자료 인증 시도")
    class AuthWithProofData {
        private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
        private final AuthenticationWithProofDataCommand command = new AuthenticationWithProofDataCommand(mentor.getId(), "proof-url");

        @Test
        @DisplayName("증명자료로 멘토 인증을 시도한다")
        void success() {
            // given
            given(mentorRepository.getById(command.mentorId())).willReturn(mentor);

            // when
            sut.authWithProofData(command);

            // then
            assertAll(
                    () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isEqualTo(command.proofDataUploadUrl()),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT)
            );
        }
    }

    private String getAuthKey(final long memberId, final String schoolMail) {
        return String.format(AUTH_KEY_PREFIX, memberId, schoolMail);
    }
}
