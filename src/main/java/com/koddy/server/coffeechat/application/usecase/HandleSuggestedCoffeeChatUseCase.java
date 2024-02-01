package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class HandleSuggestedCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final MentorRepository mentorRepository;
    private final ReservationAvailabilityChecker reservationAvailabilityChecker;

    @KoddyWritableTransactional
    public void reject(final RejectSuggestedCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getSuggestedCoffeeChat(command.coffeeChatId());
        coffeeChat.rejectFromMentorSuggest(command.rejectReason());
    }

    @KoddyWritableTransactional
    public void pending(final PendingSuggestedCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getSuggestedCoffeeChat(command.coffeeChatId());
        final Mentor mentor = mentorRepository.getByIdWithSchedules(coffeeChat.getSourceMemberId());

        reservationAvailabilityChecker.check(mentor, command.start(), command.end());
        coffeeChat.pendingFromMentorSuggest(command.question(), command.start(), command.end());
    }
}
