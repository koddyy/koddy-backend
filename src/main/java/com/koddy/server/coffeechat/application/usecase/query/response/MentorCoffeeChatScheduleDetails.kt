package com.koddy.server.coffeechat.application.usecase.query.response

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatDetails
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails
import com.koddy.server.coffeechat.domain.model.response.MenteeDetails
import com.koddy.server.global.utils.encrypt.Encryptor
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentee.Mentee

data class MentorCoffeeChatScheduleDetails(
    val mentee: MenteeDetails,
    val coffeeChat: CoffeeChatDetails,
) : CoffeeChatScheduleDetails {
    companion object {
        fun of(
            mentee: Mentee,
            languages: List<Language>,
            coffeeChat: CoffeeChat,
            encryptor: Encryptor,
        ): MentorCoffeeChatScheduleDetails =
            MentorCoffeeChatScheduleDetails(
                mentee = MenteeDetails.of(mentee, languages),
                coffeeChat = CoffeeChatDetails.of(coffeeChat, encryptor),
            )
    }
}
