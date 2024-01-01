package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.usecase.command.LogoutCommand;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class LogoutUseCase {
    private final TokenIssuer tokenIssuer;

    public void invoke(final LogoutCommand command) {
        tokenIssuer.deleteRefreshToken(command.memberId());
    }
}
