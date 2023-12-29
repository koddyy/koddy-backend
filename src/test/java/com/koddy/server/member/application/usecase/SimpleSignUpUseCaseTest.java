package com.koddy.server.member.application.usecase;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.application.usecase.command.SimpleSignUpCommand;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static com.koddy.server.member.domain.model.MemberType.MENTEE;
import static com.koddy.server.member.domain.model.MemberType.MENTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> SimpleSignUpUseCase 테스트")
class SimpleSignUpUseCaseTest extends ParallelTest {
    private final Encryptor encryptor = getEncryptor();
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final SimpleSignUpUseCase sut = new SimpleSignUpUseCase(encryptor, memberRepository);

    @Test
    @DisplayName("멘토 간편 회원가입을 진행한다")
    void mentorSimpleSignUp() {
        // given
        final SimpleSignUpCommand command = new SimpleSignUpCommand(MENTOR_1.getEmail(), "Hello123!@#", MENTOR);

        final Mentor mentor = MENTOR_1.toDomain().apply(1L);
        given(memberRepository.save(any())).willReturn(mentor);

        // when
        final Long memberId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).save(any(Mentor.class)),
                () -> assertThat(memberId).isEqualTo(mentor.getId())
        );
    }

    @Test
    @DisplayName("멘티 간편 회원가입을 진행한다")
    void menteeSimpleSignUp() {
        // given
        final SimpleSignUpCommand command = new SimpleSignUpCommand(MENTEE_1.getEmail(), "Hello123!@#", MENTEE);

        final Mentee mentee = MENTEE_1.toDomain().apply(1L);
        given(memberRepository.save(any())).willReturn(mentee);

        // when
        final Long memberId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).save(any(Mentee.class)),
                () -> assertThat(memberId).isEqualTo(mentee.getId())
        );
    }
}
