package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.command.AuthenticationConfirmWithMailCommand
import jakarta.validation.constraints.NotBlank

data class AuthenticationConfirmWithMailRequest(
    @field:NotBlank(message = "인증을 진행할 학교 메일은 필수입니다.")
    val schoolMail: String,

    @field:NotBlank(message = "인증 번호는 필수입니다.")
    val authCode: String,
) {
    fun toCommand(mentorId: Long): AuthenticationConfirmWithMailCommand =
        AuthenticationConfirmWithMailCommand(
            mentorId,
            schoolMail,
            authCode,
        )
}
