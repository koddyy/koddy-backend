package com.koddy.server.coffeechat.application.usecase.query.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatDetails;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.model.response.MentorDetails;
import com.koddy.server.global.utils.encrypt.Encryptor;
import com.koddy.server.member.domain.model.mentor.Mentor;

public record MenteeCoffeeChatScheduleDetails(
        MentorDetails mentor,
        CoffeeChatDetails coffeeChat
) implements CoffeeChatScheduleDetails {
    public static MenteeCoffeeChatScheduleDetails of(
            final Mentor mentor,
            final CoffeeChat coffeeChat,
            final Encryptor encryptor
    ) {
        return new MenteeCoffeeChatScheduleDetails(
                MentorDetails.from(mentor),
                CoffeeChatDetails.of(coffeeChat, encryptor)
        );
    }
}
