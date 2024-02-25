package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.FinallyApprovePendingCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.FinallyCancelPendingCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.utils.encrypt.Encryptor;

@UseCase
public class HandlePendingCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final Encryptor encryptor;
    private final CoffeeChatNotificationEventPublisher eventPublisher;

    public HandlePendingCoffeeChatUseCase(
            final CoffeeChatRepository coffeeChatRepository,
            final Encryptor encryptor,
            final CoffeeChatNotificationEventPublisher eventPublisher
    ) {
        this.coffeeChatRepository = coffeeChatRepository;
        this.encryptor = encryptor;
        this.eventPublisher = eventPublisher;
    }

    @KoddyWritableTransactional
    public void finallyCancel(final FinallyCancelPendingCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.mentorId());
        coffeeChat.finallyCancelPendingCoffeeChat(command.cancelReason());
        eventPublisher.publishMentorFinallyCanceledFromMentorFlowEvent(coffeeChat);
    }

    @KoddyWritableTransactional
    public void finallyApprove(final FinallyApprovePendingCoffeeChatCommand command) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.mentorId());
        coffeeChat.finallyApprovePendingCoffeeChat(Strategy.of(command.type(), command.value(), encryptor));
        eventPublisher.publishFinallyApprovedFromMentorFlowEvent(coffeeChat);
    }
}
