package com.koddy.server.file.presentation.dto.request;

import com.koddy.server.file.domain.model.PresignedFileData;
import com.koddy.server.file.utils.validator.ValidImageFile;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GetImagePresignedUrlRequest(
        @NotBlank(message = "파일명은 필수입니다.")
        @ValidImageFile
        String fileName
) {
    public PresignedFileData toFileData() {
        return new PresignedFileData(fileName);
    }
}
