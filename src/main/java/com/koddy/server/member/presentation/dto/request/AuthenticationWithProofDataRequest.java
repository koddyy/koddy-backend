package com.koddy.server.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthenticationWithProofDataRequest(
        @NotBlank(message = "증명자료 업로드 URL은 필수입니다.")
        String proofDataUploadUrl
) {
}
