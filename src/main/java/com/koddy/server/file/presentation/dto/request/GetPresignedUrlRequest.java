package com.koddy.server.file.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GetPresignedUrlRequest(
        @NotBlank(message = "파일명은 필수입니다.")
        String fileName
) {
}
