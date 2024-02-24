package com.koddy.server.member.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationWithMailRequest(
        @NotBlank(message = "인증을 진행할 학교 메일은 필수입니다.")
        String schoolMail
) {
}
