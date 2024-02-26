package com.koddy.server.member.presentation.request

import com.koddy.server.member.application.usecase.command.AuthenticationWithProofDataCommand
import jakarta.validation.constraints.NotBlank

data class AuthenticationWithProofDataRequest(
    @field:NotBlank(message = "증명자료 업로드 URL은 필수입니다.")
    val proofDataUploadUrl: String,
) {
    fun toCommand(mentorId: Long): AuthenticationWithProofDataCommand =
        AuthenticationWithProofDataCommand(
            mentorId,
            proofDataUploadUrl,
        )
}
