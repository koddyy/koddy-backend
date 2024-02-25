package com.koddy.server.member.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationWithProofDataRequest(
        @NotBlank(message = "증명자료 업로드 URL은 필수입니다.")
        String proofDataUploadUrl
) {
}
