package com.koddy.server.coffeechat.application.usecase.query.response

import com.koddy.server.coffeechat.domain.model.response.CoffeeChatSimpleDetails
import com.koddy.server.coffeechat.domain.model.response.MenteeSimpleDetails
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData

data class MentorCoffeeChatSchedule(
    val coffeeChat: CoffeeChatSimpleDetails,
    val mentee: MenteeSimpleDetails,
) {
    companion object {
        fun from(data: MentorCoffeeChatScheduleData): MentorCoffeeChatSchedule {
            return MentorCoffeeChatSchedule(
                coffeeChat = CoffeeChatSimpleDetails(
                    id = data.id,
                    status = data.status.name,
                ),
                mentee = MenteeSimpleDetails(
                    id = data.menteeId,
                    name = data.menteeName,
                    profileImageUrl = data.menteeProfileImageUrl,
                    interestSchool = data.menteeInterest.school,
                    interestMajor = data.menteeInterest.major,
                ),
            )
        }
    }
}
