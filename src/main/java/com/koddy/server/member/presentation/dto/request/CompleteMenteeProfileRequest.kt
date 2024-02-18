package com.koddy.server.member.presentation.dto.request

import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand

data class CompleteMenteeProfileRequest(
    val introduction: String?,
    val profileImageUrl: String?,
) {
    fun toCommand(menteeId: Long): CompleteMenteeProfileCommand =
        CompleteMenteeProfileCommand(
            menteeId,
            introduction,
            profileImageUrl,
        )
}
