package com.koddy.server.member.application.usecase;

import com.koddy.server.common.UseCaseTest;
import com.koddy.server.member.application.usecase.command.DeleteMemberCommand;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.service.MenteeDeleter;
import com.koddy.server.member.domain.service.MentorDeleter;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> DeleteMemberUseCase 테스트")
class DeleteMemberUseCaseTest extends UseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MentorDeleter mentorDeleter = mock(MentorDeleter.class);
    private final MenteeDeleter menteeDeleter = mock(MenteeDeleter.class);
    private final DeleteMemberUseCase sut = new DeleteMemberUseCase(
            memberRepository,
            mentorDeleter,
            menteeDeleter
    );

    private final DeleteMemberCommand command = new DeleteMemberCommand(1);

    @Test
    @DisplayName("존재하지 않는 사용자는 삭제할 수 없다")
    void throwExceptionByMemberNotFound() {
        // given
        given(memberRepository.existsById(command.memberId())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsById(command.memberId()),
                () -> verify(memberRepository, times(0)).isMentor(command.memberId()),
                () -> verify(mentorDeleter, times(0)).execute(command.memberId()),
                () -> verify(menteeDeleter, times(0)).execute(command.memberId())
        );
    }

    @Test
    @DisplayName("Mentor를 삭제한다")
    void deleteMentor() {
        // given
        given(memberRepository.existsById(command.memberId())).willReturn(true);
        given(memberRepository.isMentor(command.memberId())).willReturn(true);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).existsById(command.memberId()),
                () -> verify(memberRepository, times(1)).isMentor(command.memberId()),
                () -> verify(mentorDeleter, times(1)).execute(command.memberId()),
                () -> verify(menteeDeleter, times(0)).execute(command.memberId())
        );
    }

    @Test
    @DisplayName("Mentee를 삭제한다")
    void deleteMentee() {
        // given
        given(memberRepository.existsById(command.memberId())).willReturn(true);
        given(memberRepository.isMentor(command.memberId())).willReturn(false);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).existsById(command.memberId()),
                () -> verify(memberRepository, times(1)).isMentor(command.memberId()),
                () -> verify(mentorDeleter, times(0)).execute(command.memberId()),
                () -> verify(menteeDeleter, times(1)).execute(command.memberId())
        );
    }
}
