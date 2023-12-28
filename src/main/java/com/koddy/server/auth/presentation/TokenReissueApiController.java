package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.ReissueTokenUseCase;
import com.koddy.server.auth.application.usecase.command.ReissueTokenCommand;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.utils.TokenResponseWriter;
import com.koddy.server.global.annotation.ExtractToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.auth.domain.model.TokenType.REFRESH;

@Tag(name = "토큰 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final TokenResponseWriter tokenResponseWriter;

    @Operation(summary = "RefreshToken을 통한 토큰 재발급 Endpoint")
    @PostMapping
    public ResponseEntity<Void> reissueToken(
            @ExtractToken(tokenType = REFRESH) final String refreshToken,
            final HttpServletResponse response
    ) {
        final AuthToken authToken = reissueTokenUseCase.invoke(new ReissueTokenCommand(refreshToken));
        tokenResponseWriter.applyToken(response, authToken);

        return ResponseEntity.noContent().build();
    }
}
