package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class HandleSuggestedCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;

    @KoddyWritableTransactional
    public void reject(final RejectSuggestedCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId());
        coffeeChat.rejectFromMentorSuggest(command.rejectReason());
    }

    @KoddyWritableTransactional
    public void pending(final PendingSuggestedCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId());
        coffeeChat.pendingFromMentorSuggest(command.question(), command.start(), command.end());
    }
}
