package com.koddy.server.member.application.usecase.command;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.MemberType;

public record SimpleSignUpCommand(
        Email email,
        String password,
        MemberType type
) {
}
