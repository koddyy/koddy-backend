package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_CANCEL_STATUS;

@UseCase
@RequiredArgsConstructor
public class CancelCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;

    @KoddyWritableTransactional
    public void invoke(final CancelCoffeeChatCommand command) {
        final Authenticated authenticated = command.authenticated();

        if (authenticated.isMentor()) {
            cancelSuggestedCoffeeChat(command, authenticated);
        } else {
            cancelAppliedCoffeeChat(command, authenticated);
        }
    }

    private void cancelSuggestedCoffeeChat(final CancelCoffeeChatCommand command, final Authenticated authenticated) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), authenticated.id());
        if (coffeeChat.isMentorCannotCancel()) {
            throw new CoffeeChatException(CANNOT_CANCEL_STATUS);
        }
        coffeeChat.cancel(MENTOR_CANCEL, command.cancelReason());
    }

    private void cancelAppliedCoffeeChat(final CancelCoffeeChatCommand command, final Authenticated authenticated) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMenteeId(command.coffeeChatId(), authenticated.id());
        if (coffeeChat.isMenteeCannotCancel()) {
            throw new CoffeeChatException(CANNOT_CANCEL_STATUS);
        }
        coffeeChat.cancel(MENTEE_CANCEL, command.cancelReason());
    }
}
