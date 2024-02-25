package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatByApplyCommand;
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatBySuggestCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher;
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;

@UseCase
public class CreateCoffeeChatUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final ReservationAvailabilityChecker reservationAvailabilityChecker;
    private final CoffeeChatRepository coffeeChatRepository;
    private final CoffeeChatNotificationEventPublisher eventPublisher;

    public CreateCoffeeChatUseCase(
            final MentorRepository mentorRepository,
            final MenteeRepository menteeRepository,
            final ReservationAvailabilityChecker reservationAvailabilityChecker,
            final CoffeeChatRepository coffeeChatRepository,
            final CoffeeChatNotificationEventPublisher eventPublisher
    ) {
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
        this.reservationAvailabilityChecker = reservationAvailabilityChecker;
        this.coffeeChatRepository = coffeeChatRepository;
        this.eventPublisher = eventPublisher;
    }

    @KoddyWritableTransactional
    public long createByApply(final CreateCoffeeChatByApplyCommand command) {
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        reservationAvailabilityChecker.check(mentor, command.reservation());

        final CoffeeChat coffeeChat = coffeeChatRepository.save(CoffeeChat.apply(mentee, mentor, command.applyReason(), command.reservation()));
        eventPublisher.publishMenteeAppliedFromMenteeFlowEvent(coffeeChat);
        return coffeeChat.getId();
    }

    @KoddyWritableTransactional
    public long createBySuggest(final CreateCoffeeChatBySuggestCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        final Mentee mentee = menteeRepository.getById(command.menteeId());

        final CoffeeChat coffeeChat = coffeeChatRepository.save(CoffeeChat.suggest(mentor, mentee, command.suggestReason()));
        eventPublisher.publishMentorSuggestedFromMentorFlowEvent(coffeeChat);
        return coffeeChat.getId();
    }
}
