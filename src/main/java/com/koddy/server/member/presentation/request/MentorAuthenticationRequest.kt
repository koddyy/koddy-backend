package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.command.AttemptWithMailCommand
import com.koddy.server.member.application.usecase.command.AttemptWithProofDataCommand
import com.koddy.server.member.application.usecase.command.ConfirmMailAuthCodeCommand
import jakarta.validation.constraints.NotBlank

data class AuthenticationWithMailRequest(
    @field:NotBlank(message = "인증을 진행할 학교 메일은 필수입니다.")
    val schoolMail: String,
) {
    fun toCommand(mentorId: Long): AttemptWithMailCommand =
        AttemptWithMailCommand(
            mentorId = mentorId,
            schoolMail = schoolMail,
        )
}

data class AuthenticationConfirmWithMailRequest(
    @field:NotBlank(message = "인증을 진행할 학교 메일은 필수입니다.")
    val schoolMail: String,

    @field:NotBlank(message = "인증 번호는 필수입니다.")
    val authCode: String,
) {
    fun toCommand(mentorId: Long): ConfirmMailAuthCodeCommand =
        ConfirmMailAuthCodeCommand(
            mentorId = mentorId,
            schoolMail = schoolMail,
            authCode = authCode,
        )
}

data class AuthenticationWithProofDataRequest(
    @field:NotBlank(message = "증명자료 업로드 URL은 필수입니다.")
    val proofDataUploadUrl: String,
) {
    fun toCommand(mentorId: Long): AttemptWithProofDataCommand =
        AttemptWithProofDataCommand(
            mentorId = mentorId,
            proofDataUploadUrl = proofDataUploadUrl,
        )
}
