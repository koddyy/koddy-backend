package com.koddy.server.coffeechat.application.usecase.query.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatDetails;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.model.response.MenteeDetails;
import com.koddy.server.global.utils.encrypt.Encryptor;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;

import java.util.List;

public record MentorCoffeeChatScheduleDetails(
        MenteeDetails mentee,
        CoffeeChatDetails coffeeChat
) implements CoffeeChatScheduleDetails {
    public static MentorCoffeeChatScheduleDetails of(
            final Mentee mentee,
            final List<Language> languages,
            final CoffeeChat coffeeChat,
            final Encryptor encryptor
    ) {
        return new MentorCoffeeChatScheduleDetails(
                MenteeDetails.of(mentee, languages),
                CoffeeChatDetails.of(coffeeChat, encryptor)
        );
    }
}
