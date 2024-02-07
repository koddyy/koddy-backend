package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.MenteeApplyCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.MentorSuggestCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CreateCoffeeChatUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final ReservationAvailabilityChecker reservationAvailabilityChecker;
    private final CoffeeChatRepository coffeeChatRepository;

    @KoddyWritableTransactional
    public long suggestCoffeeChat(final MentorSuggestCoffeeChatCommand command) {
        final Mentor mentor = mentorRepository.getById(command.mentorId());
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        return coffeeChatRepository.save(CoffeeChat.suggest(mentor, mentee, command.applyReason())).getId();
    }

    @KoddyWritableTransactional
    public long applyCoffeeChat(final MenteeApplyCoffeeChatCommand command) {
        final Mentee mentee = menteeRepository.getById(command.menteeId());
        final Mentor mentor = mentorRepository.getById(command.mentorId());

        reservationAvailabilityChecker.check(mentor, command.reservation());
        return coffeeChatRepository.save(CoffeeChat.apply(
                mentee,
                mentor,
                command.applyReason(),
                command.reservation()
        )).getId();
    }
}
