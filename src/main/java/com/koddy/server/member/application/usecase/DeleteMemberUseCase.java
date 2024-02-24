package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.service.MenteeDeleter;
import com.koddy.server.member.domain.service.MentorDeleter;

@UseCase
public class DeleteMemberUseCase {
    private final MemberRepository memberRepository;
    private final MentorDeleter mentorDeleter;
    private final MenteeDeleter menteeDeleter;

    public DeleteMemberUseCase(
            final MemberRepository memberRepository,
            final MentorDeleter mentorDeleter,
            final MenteeDeleter menteeDeleter
    ) {
        this.memberRepository = memberRepository;
        this.mentorDeleter = mentorDeleter;
        this.menteeDeleter = menteeDeleter;
    }

    @KoddyWritableTransactional
    public void invoke(final long memberId) {
        final Member<?> member = memberRepository.getById(memberId);

        if (isMentor(member)) {
            mentorDeleter.execute(member.getId());
        } else {
            menteeDeleter.execute(member.getId());
        }
    }

    private boolean isMentor(final Member<?> member) {
        return member instanceof Mentor;
    }
}
