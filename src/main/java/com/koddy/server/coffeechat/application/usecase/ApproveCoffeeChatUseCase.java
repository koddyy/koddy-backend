package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.ApproveMenteeApplyCommand;
import com.koddy.server.coffeechat.application.usecase.command.ApproveMentorSuggestCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.encrypt.Encryptor;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ApproveCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final Encryptor encryptor;

    // TODO delete
    @KoddyWritableTransactional
    public void suggestByMentor(final ApproveMentorSuggestCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId());
//        coffeeChat.approveMentorSuggest(command.start(), command.end());
    }

    // TODO delete
    @KoddyWritableTransactional
    public void applyByMentee(final ApproveMenteeApplyCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId());
//        coffeeChat.approveMenteeApply(Strategy.of(command.type(), command.value(), encryptor));
    }
}
