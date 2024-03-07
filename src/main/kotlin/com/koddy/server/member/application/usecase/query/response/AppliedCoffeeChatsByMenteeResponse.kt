package com.koddy.server.member.application.usecase.query.response

import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee

data class AppliedCoffeeChatsByMenteeResponse(
    val coffeeChatId: Long,
    val menteeId: Long,
    val name: String,
    val profileImageUrl: String?,
    val nationality: String,
    val interestSchool: String,
    val interestMajor: String,
) {
    companion object {
        fun from(result: AppliedCoffeeChatsByMentee): AppliedCoffeeChatsByMenteeResponse {
            return AppliedCoffeeChatsByMenteeResponse(
                coffeeChatId = result.coffeeChatId,
                menteeId = result.menteeId,
                name = result.name,
                profileImageUrl = result.profileImageUrl,
                nationality = result.nationality.code,
                interestSchool = result.interest.school,
                interestMajor = result.interest.major,
            )
        }
    }
}
