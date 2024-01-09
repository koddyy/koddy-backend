package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.DeleteMemberCommand;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.service.MenteeDeleter;
import com.koddy.server.member.domain.service.MentorDeleter;
import com.koddy.server.member.exception.MemberException;
import lombok.RequiredArgsConstructor;

import static com.koddy.server.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@UseCase
@RequiredArgsConstructor
public class DeleteMemberUseCase {
    private final MemberRepository memberRepository;
    private final MentorDeleter mentorDeleter;
    private final MenteeDeleter menteeDeleter;

    public void invoke(final DeleteMemberCommand command) {
        validateMemberExists(command.memberId());

        if (memberRepository.isMentor(command.memberId())) {
            mentorDeleter.execute(command.memberId());
        } else {
            menteeDeleter.execute(command.memberId());
        }
    }

    private void validateMemberExists(final long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberException(MEMBER_NOT_FOUND);
        }
    }
}
