package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Password;

public record SimpleSignUpCommand(
        Email email,
        Password password
) {
}
