package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.query.GetCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.utils.encrypt.Encryptor;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetCoffeeChatScheduleDetailsUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final Encryptor encryptor;

    @KoddyReadOnlyTransactional
    public CoffeeChatScheduleDetails invoke(final GetCoffeeChatScheduleDetails query) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getById(query.coffeeChatId());
        final Mentor mentor = mentorRepository.getById(coffeeChat.getMentorId());
        final Mentee mentee = menteeRepository.getById(coffeeChat.getMenteeId());

        if (query.authenticated().isMentor()) {
            return MentorCoffeeChatScheduleDetails.of(mentee, coffeeChat, encryptor);
        }
        return MenteeCoffeeChatScheduleDetails.of(mentor, coffeeChat, encryptor);
    }
}
