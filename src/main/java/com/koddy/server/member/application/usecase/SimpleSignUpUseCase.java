package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.SimpleSignUpCommand;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class SimpleSignUpUseCase {
    public void invoke(final SimpleSignUpCommand command) {
        // TODO
    }
}
