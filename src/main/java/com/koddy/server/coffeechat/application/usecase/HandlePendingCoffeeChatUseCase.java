package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.ApprovePendingCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectPendingCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.utils.encrypt.Encryptor;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class HandlePendingCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final Encryptor encryptor;

    @KoddyWritableTransactional
    public void reject(final RejectPendingCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getPendingCoffeeChat(command.coffeeChatId());
        coffeeChat.rejectPendingCoffeeChat(command.rejectReason());
    }

    @KoddyWritableTransactional
    public void approve(final ApprovePendingCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getPendingCoffeeChat(command.coffeeChatId());
        coffeeChat.approvePendingCoffeeChat(Strategy.of(command.type(), command.value(), encryptor));
    }
}
