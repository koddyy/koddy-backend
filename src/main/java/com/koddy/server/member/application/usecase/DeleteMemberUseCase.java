package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.DeleteMemberCommand;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DeleteMemberUseCase {
    public void invoke(final DeleteMemberCommand command) {

    }
}
