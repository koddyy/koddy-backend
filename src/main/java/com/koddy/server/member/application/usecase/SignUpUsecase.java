package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.command.SignUpMenteeCommand;
import com.koddy.server.member.application.usecase.command.SignUpMentorCommand;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class SignUpUsecase {
    public void signUpMentor(final SignUpMentorCommand command) {

    }

    public void signUpMentee(final SignUpMenteeCommand command) {

    }
}
