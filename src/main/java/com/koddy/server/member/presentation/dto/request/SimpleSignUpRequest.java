package com.koddy.server.member.presentation.dto.request;

import com.koddy.server.member.domain.model.MemberType;
import com.koddy.server.member.utils.validator.ValidMailCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SimpleSignUpRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @ValidMailCheck
        Boolean checked,

        @NotBlank(message = "패스워드는 필수입니다.")
        String password,

        @NotNull(message = "회원 타입은 필수입니다.")
        MemberType type
) {
}
