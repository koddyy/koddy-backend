package com.koddy.server.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConfirmAuthCodeRequest(
        @NotBlank(message = "인증 코드는 필수입니다.")
        String authCode
) {
}
