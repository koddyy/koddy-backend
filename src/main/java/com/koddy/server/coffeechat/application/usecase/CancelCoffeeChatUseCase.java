package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL;

@UseCase
@RequiredArgsConstructor
public class CancelCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;

    @KoddyWritableTransactional
    public void invoke(final CancelCoffeeChatCommand command) {
        final Authenticated authenticated = command.authenticated();

        if (authenticated.isMentor()) {
            cancelSuggestedCoffeeChat(command.coffeeChatId(), authenticated.id());
        } else {
            cancelAppliedCoffeeChat(command.coffeeChatId(), authenticated.id());
        }
    }

    private void cancelSuggestedCoffeeChat(final long coffeeChatId, final long mentorId) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMentorId(coffeeChatId, mentorId);
        coffeeChat.cancel(MENTOR_CANCEL);
    }

    private void cancelAppliedCoffeeChat(final long coffeeChatId, final long menteeId) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMenteeId(coffeeChatId, menteeId);
        coffeeChat.cancel(MENTEE_CANCEL);
    }
}
