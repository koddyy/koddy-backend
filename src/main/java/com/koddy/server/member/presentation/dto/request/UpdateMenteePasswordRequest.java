package com.koddy.server.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateMenteePasswordRequest(
        @NotBlank(message = "기존 비밀번호는 필수입니다.")
        String currentPassword,

        @NotBlank(message = "변경할 비밀번호는 필수입니다.")
        String updatePassword
) {
}
