package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.DeleteMemberCommand;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DeleteMemberUseCase {
    private final MemberRepository memberRepository;

    public void invoke(final DeleteMemberCommand command) {

    }
}
