package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CancelCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;

    @KoddyWritableTransactional
    public void invoke(final CancelCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId());
        coffeeChat.cancel();
    }
}
