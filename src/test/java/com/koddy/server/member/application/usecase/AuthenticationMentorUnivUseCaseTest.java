package com.koddy.server.member.application.usecase;

import com.koddy.server.auth.domain.model.code.AuthKeyGenerator;
import com.koddy.server.common.UseCaseTest;
import com.koddy.server.common.mock.stub.StubAuthenticationProcessor;
import com.koddy.server.member.application.usecase.command.AuthenticationWithMailCommand;
import com.koddy.server.member.domain.event.MailAuthenticatedEvent;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.mentor.AuthenticationStatus.ATTEMPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> AuthenticationMentorUnivUseCase 테스트")
class AuthenticationMentorUnivUseCaseTest extends UseCaseTest {
    private static final String AUTH_KEY_PREFIX = "MENTOR-MAIL-AUTH:%s";
    private static final String AUTH_CODE = "123456";

    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final AuthKeyGenerator authKeyGenerator = (prefix, suffix) -> String.format(AUTH_KEY_PREFIX, suffix);
    private final StubAuthenticationProcessor authenticationProcessor = new StubAuthenticationProcessor(() -> AUTH_CODE);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final AuthenticationMentorUnivUseCase sut = new AuthenticationMentorUnivUseCase(
            mentorRepository,
            authKeyGenerator,
            authenticationProcessor,
            eventPublisher
    );

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);

    @Nested
    @DisplayName("학교 메일 인증")
    class AuthWithMail {
        private final String schoolMail = "sjiwon@kyonggi.ac.kr";
        private final AuthenticationWithMailCommand command = new AuthenticationWithMailCommand(mentor.getId(), schoolMail);

        @Test
        @DisplayName("학교 메일로 멘토 인증을 진행한다")
        void success() {
            // given
            given(mentorRepository.getById(command.mentorId())).willReturn(mentor);

            // when
            sut.authWithMail(command);

            // then
            assertAll(
                    () -> verify(mentorRepository, times(1)).getById(command.mentorId()),
                    () -> verify(eventPublisher, times(1)).publishEvent(any(MailAuthenticatedEvent.class)),
                    () -> {
                        final Map<String, String> cache = authenticationProcessor.getCache();
                        final String key = String.format(AUTH_KEY_PREFIX, command.schoolMail());
                        assertThat(cache.get(key)).isEqualTo(AUTH_CODE);
                    },
                    () -> assertThat(mentor.getUniversityAuthentication().getSchoolMail()).isEqualTo(schoolMail),
                    () -> assertThat(mentor.getUniversityAuthentication().getProofDataUploadUrl()).isNull(),
                    () -> assertThat(mentor.getUniversityAuthentication().getStatus()).isEqualTo(ATTEMPT)
            );
        }
    }
}
