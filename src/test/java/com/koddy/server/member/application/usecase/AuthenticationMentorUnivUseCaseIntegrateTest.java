package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import com.koddy.server.auth.domain.model.code.AuthKeyGenerator;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.IntegrateTest;
import com.koddy.server.common.mock.stub.StubEmailSender;
import com.koddy.server.global.redis.RedisOperator;
import com.koddy.server.mail.application.adapter.EmailSender;
import com.koddy.server.member.application.usecase.command.AuthenticationConfirmWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithMailCommand;
import com.koddy.server.member.application.usecase.command.AuthenticationWithProofDataCommand;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.ATTEMPT;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.COMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> AuthenticationMentorUnivUseCase 테스트 [IntegrateTest]")
class AuthenticationMentorUnivUseCaseIntegrateTest extends IntegrateTest {
    @Autowired
    private AuthenticationMentorUnivUseCase sut;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private RedisOperator<String, String> redisOperator;

    private static final String AUTH_KEY_PREFIX = "MENTOR-MAIL-AUTH:%s";
    private static final String AUTH_CODE = "123456";

    @TestConfiguration
    static class AuthenticationMentorUnivUseCaseIntegrateTestConfig {
        @Bean
        public AuthKeyGenerator authKeyGenerator() {
            return (prefix, suffix) -> String.format(AUTH_KEY_PREFIX, suffix);
        }

        @Bean
        public AuthCodeGenerator authCodeGenerator() {
            return () -> AUTH_CODE;
        }

        @Bean
        public EmailSender emailSender() {
            return new StubEmailSender();
        }
    }

    @Nested
    @DisplayName("학교 메일 인증 시도")
    class AuthWithMail {
        @Test
        @DisplayName("학교 메일로 멘토 인증을 시도한다")
        void success() {
            // given
            final Mentor mentor = mentorRepository.save(MENTOR_1.toDomain());
            final String schoolMail = "sjiwon@kyonggi.ac.kr";
            final AuthenticationWithMailCommand command = new AuthenticationWithMailCommand(mentor.getId(), schoolMail);

            // when
            sut.authWithMail(command);

            // then
            final Mentor findMentor = mentorRepository.getById(mentor.getId());
            assertAll(
                    () -> assertThat(findMentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(findMentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(findMentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT),
                    () -> {
                        final String authKey = getAuthKey(command.schoolMail());
                        assertThat(redisOperator.get(authKey)).isEqualTo(AUTH_CODE);
                    }
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
            final Mentor mentor = mentorRepository.save(MENTOR_1.toDomain());
            final String schoolMail = "sjiwon@kyonggi.ac.kr";
            sut.authWithMail(new AuthenticationWithMailCommand(mentor.getId(), schoolMail));

            final AuthenticationConfirmWithMailCommand command = new AuthenticationConfirmWithMailCommand(
                    mentor.getId(),
                    schoolMail,
                    AUTH_CODE + "diff"
            );

            // when - then
            assertThatThrownBy(() -> sut.confirmMailAuthCode(command))
                    .isInstanceOf(AuthException.class)
                    .hasMessage(INVALID_AUTH_CODE.getMessage());

            final Mentor findMentor = mentorRepository.getById(mentor.getId());
            assertAll(
                    () -> assertThat(findMentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(findMentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(findMentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT),
                    () -> {
                        final String authKey = getAuthKey(command.schoolMail());
                        assertThat(redisOperator.get(authKey)).isEqualTo(AUTH_CODE);
                    }
            );
        }

        @Test
        @DisplayName("인증번호가 일치하고 그에 따라서 학교 메일 인증에 성공한다")
        void success() {
            // given
            final Mentor mentor = mentorRepository.save(MENTOR_1.toDomain());
            final String schoolMail = "sjiwon@kyonggi.ac.kr";
            sut.authWithMail(new AuthenticationWithMailCommand(mentor.getId(), schoolMail));

            final AuthenticationConfirmWithMailCommand command = new AuthenticationConfirmWithMailCommand(
                    mentor.getId(),
                    schoolMail,
                    AUTH_CODE
            );

            // when
            sut.confirmMailAuthCode(command);

            // then
            final Mentor findMentor = mentorRepository.getById(mentor.getId());
            assertAll(
                    () -> assertThat(findMentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(findMentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(findMentor.getUniversityAuthentication().getStatus()).isEqualTo(COMPLETE),
                    () -> {
                        final String authKey = getAuthKey(command.schoolMail());
                        assertThat(redisOperator.get(authKey)).isNull(); // 인증성공하면 바로 제거
                    }
            );
        }
    }

    @Nested
    @DisplayName("학교 증명자료 인증 시도")
    class AuthWithProofData {
        @Test
        @DisplayName("증명자료로 멘토 인증을 시도한다")
        void success() {
            // given
            final Mentor mentor = mentorRepository.save(MENTOR_1.toDomain());
            final AuthenticationWithProofDataCommand command = new AuthenticationWithProofDataCommand(mentor.getId(), "proof-url");

            // when
            sut.authWithProofData(command);

            // then
            final Mentor findMentor = mentorRepository.getById(mentor.getId());
            assertAll(
                    () -> assertThat(findMentor.getUniversityAuthentication().getSchoolMail()).isNull(),
                    () -> assertThat(findMentor.getUniversityAuthentication().getProofDataUploadUrl()).isEqualTo(command.proofDataUploadUrl()),
                    () -> assertThat(findMentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT)
            );
        }
    }

    private String getAuthKey(final String schoolMail) {
        return String.format(AUTH_KEY_PREFIX, schoolMail);
    }
}
