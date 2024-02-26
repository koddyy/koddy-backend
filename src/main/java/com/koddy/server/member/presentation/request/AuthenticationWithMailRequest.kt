package com.koddy.server.member.presentation.request

import jakarta.validation.constraints.NotBlank

data class AuthenticationWithMailRequest(
    @field:NotBlank(message = "인증을 진행할 학교 메일은 필수입니다.")
    val schoolMail: String,
)
