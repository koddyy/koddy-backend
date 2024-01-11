package com.koddy.server.auth.application.usecase;

import com.koddy.server.auth.application.usecase.command.ReissueTokenCommand;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.domain.service.TokenIssuer;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.utils.TokenProvider;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN;

@UseCase
@RequiredArgsConstructor
public class ReissueTokenUseCase {
    private final TokenProvider tokenProvider;
    private final TokenIssuer tokenIssuer;

    public AuthToken invoke(final ReissueTokenCommand command) {
        final long memberId = tokenProvider.getId(command.refreshToken());
        validateMemberToken(memberId, command.refreshToken());
        return tokenIssuer.reissueAuthorityToken(memberId);
    }

    private void validateMemberToken(final long memberId, final String refreshToken) {
        if (isAnonymousRefreshToken(memberId, refreshToken)) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    private boolean isAnonymousRefreshToken(final long memberId, final String refreshToken) {
        return !tokenIssuer.isMemberRefreshToken(memberId, refreshToken);
    }
}
