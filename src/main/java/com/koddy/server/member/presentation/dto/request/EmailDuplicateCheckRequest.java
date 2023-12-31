package com.koddy.server.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailDuplicateCheckRequest(
        @NotBlank(message = "중복 체크할 이메일은 필수입니다.")
        String value
) {
}
