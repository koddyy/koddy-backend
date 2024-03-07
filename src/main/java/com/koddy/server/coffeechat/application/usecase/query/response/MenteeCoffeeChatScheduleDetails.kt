package com.koddy.server.coffeechat.application.usecase.query.response

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatDetails
import com.koddy.server.coffeechat.domain.model.response.MentorDetails
import com.koddy.server.global.utils.encrypt.Encryptor
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentor.Mentor

data class MenteeCoffeeChatScheduleDetails(
    val mentor: MentorDetails,
    val coffeeChat: CoffeeChatDetails,
) : CoffeeChatScheduleDetails {
    companion object {
        fun of(
            mentor: Mentor,
            languages: List<Language>,
            coffeeChat: CoffeeChat,
            encryptor: Encryptor,
        ): MenteeCoffeeChatScheduleDetails {
            return MenteeCoffeeChatScheduleDetails(
                mentor = MentorDetails.of(mentor, languages),
                coffeeChat = CoffeeChatDetails.of(coffeeChat, encryptor),
            )
        }
    }
}
