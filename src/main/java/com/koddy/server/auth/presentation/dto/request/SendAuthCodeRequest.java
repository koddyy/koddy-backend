package com.koddy.server.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SendAuthCodeRequest(
        @NotBlank(message = "인증을 진행할 이메일은 필수입니다.")
        String email
) {
}
