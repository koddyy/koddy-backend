package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.ApproveAppliedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectAppliedCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.utils.encrypt.Encryptor;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class HandleMenteeAppliedCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final Encryptor encryptor;

    @KoddyWritableTransactional
    public void reject(final RejectAppliedCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getMenteeAppliedCoffeeChat(command.coffeeChatId(), command.mentorId());
        coffeeChat.rejectFromMenteeApply(command.rejectReason());
    }

    @KoddyWritableTransactional
    public void approve(final ApproveAppliedCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getMenteeAppliedCoffeeChat(command.coffeeChatId(), command.mentorId());
        coffeeChat.approveFromMenteeApply(Strategy.of(command.type(), command.value(), encryptor));
    }
}
