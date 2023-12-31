package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.EmailAuthenticationUseCase;
import com.koddy.server.auth.application.usecase.command.ConfirmAuthCodeCommand;
import com.koddy.server.auth.application.usecase.command.SendAuthCodeCommand;
import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.presentation.dto.request.ConfirmAuthCodeRequest;
import com.koddy.server.global.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이메일 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/email")
public class EmailAuthenticationApiController {
    private final EmailAuthenticationUseCase emailAuthenticationUseCase;

    @Operation(summary = "인증번호 발송 Endpoint")
    @PostMapping
    public ResponseEntity<Void> sendAuthCode(
            @Auth final Authenticated authenticated
    ) {
        emailAuthenticationUseCase.sendAuthCode(new SendAuthCodeCommand(authenticated.id()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "인증번호 확인 Endpoint")
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmAuthCode(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final ConfirmAuthCodeRequest request
    ) {
        emailAuthenticationUseCase.confirmAuthCode(new ConfirmAuthCodeCommand(
                authenticated.id(),
                request.authCode()
        ));
        return ResponseEntity.noContent().build();
    }
}
