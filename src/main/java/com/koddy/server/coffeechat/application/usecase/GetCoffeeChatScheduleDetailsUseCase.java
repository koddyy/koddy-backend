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
import com.koddy.server.member.domain.model.AvailableLanguage;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.AvailableLanguageRepository;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;

import java.util.List;

@UseCase
public class GetCoffeeChatScheduleDetailsUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final AvailableLanguageRepository availableLanguageRepository;
    private final Encryptor encryptor;

    public GetCoffeeChatScheduleDetailsUseCase(
            final CoffeeChatRepository coffeeChatRepository,
            final MentorRepository mentorRepository,
            final MenteeRepository menteeRepository,
            final AvailableLanguageRepository availableLanguageRepository,
            final Encryptor encryptor
    ) {
        this.coffeeChatRepository = coffeeChatRepository;
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
        this.availableLanguageRepository = availableLanguageRepository;
        this.encryptor = encryptor;
    }

    @KoddyReadOnlyTransactional
    public CoffeeChatScheduleDetails invoke(final GetCoffeeChatScheduleDetails query) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getById(query.coffeeChatId());

        if (query.authenticated().isMentor()) {
            final Mentee mentee = menteeRepository.getByIdWithNative(coffeeChat.getMenteeId());
            final List<Language> languages = fetchMemberLanguages(mentee.getId());
            return MentorCoffeeChatScheduleDetails.of(mentee, languages, coffeeChat, encryptor);
        } else {
            final Mentor mentor = mentorRepository.getByIdWithNative(coffeeChat.getMentorId());
            final List<Language> languages = fetchMemberLanguages(mentor.getId());
            return MenteeCoffeeChatScheduleDetails.of(mentor, languages, coffeeChat, encryptor);
        }
    }

    private List<Language> fetchMemberLanguages(final long memberId) {
        return availableLanguageRepository.findByMemberIdWithNative(memberId)
                .stream()
                .map(AvailableLanguage::getLanguage)
                .toList();
    }
}
