package com.koddy.server.file.presentation.dto.request;

import com.koddy.server.file.utils.validator.ValidPdfFile;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GetPdfPresignedUrlRequest(
        @NotBlank(message = "파일명은 필수입니다.")
        @ValidPdfFile
        String fileName
) {
}