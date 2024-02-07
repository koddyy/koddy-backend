package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
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
        final CoffeeChat coffeeChat = coffeeChatRepository.getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.authenticated().id());
        coffeeChat.cancel(decideStatus(command.authenticated()));
    }

    private CoffeeChatStatus decideStatus(final Authenticated authenticated) {
        if (authenticated.isMentor()) {
            return MENTOR_CANCEL;
        }
        return MENTEE_CANCEL;
    }
}
