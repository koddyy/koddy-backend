package com.koddy.server.coffeechat.application.usecase.query.response

import com.koddy.server.coffeechat.domain.model.response.CoffeeChatSimpleDetails
import com.koddy.server.coffeechat.domain.model.response.MentorSimpleDetails
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData

data class MenteeCoffeeChatSchedule(
    val coffeeChat: CoffeeChatSimpleDetails,
    val mentor: MentorSimpleDetails,
) {
    companion object {
        fun from(data: MenteeCoffeeChatScheduleData): MenteeCoffeeChatSchedule {
            return MenteeCoffeeChatSchedule(
                coffeeChat = CoffeeChatSimpleDetails(
                    id = data.id,
                    status = data.status.name,
                ),
                mentor = MentorSimpleDetails(
                    id = data.mentorId,
                    name = data.mentorName,
                    profileImageUrl = data.mentorProfileImageUrl,
                    school = data.mentorUniversityProfile.school,
                    major = data.mentorUniversityProfile.major,
                    enteredIn = data.mentorUniversityProfile.enteredIn,
                ),
            )
        }
    }
}
