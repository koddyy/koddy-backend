package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.global.annotation.UseCase;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW;

@UseCase
public class CancelCoffeeChatUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final CoffeeChatNotificationEventPublisher eventPublisher;

    public CancelCoffeeChatUseCase(
            final CoffeeChatRepository coffeeChatRepository,
            final CoffeeChatNotificationEventPublisher eventPublisher
    ) {
        this.coffeeChatRepository = coffeeChatRepository;
        this.eventPublisher = eventPublisher;
    }

    @KoddyWritableTransactional
    public void invoke(final CancelCoffeeChatCommand command) {
        final Authenticated authenticated = command.authenticated();

        if (authenticated.isMentor()) {
            cancelByMentor(command, authenticated);
        } else {
            cancelByMentee(command, authenticated);
        }
    }

    private void cancelByMentor(final CancelCoffeeChatCommand command, final Authenticated authenticated) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), authenticated.id);
        coffeeChat.cancel(determineCancelStatus(coffeeChat), authenticated.id, command.cancelReason());

        if (coffeeChat.isMenteeFlow()) {
            eventPublisher.publishMentorCanceledFromMenteeFlowEvent(coffeeChat);
        } else {
            eventPublisher.publishMentorCanceledFromMentorFlowEvent(coffeeChat);
        }
    }

    private void cancelByMentee(final CancelCoffeeChatCommand command, final Authenticated authenticated) {
        final CoffeeChat coffeeChat = coffeeChatRepository.getByIdAndMenteeId(command.coffeeChatId(), authenticated.id);
        coffeeChat.cancel(determineCancelStatus(coffeeChat), authenticated.id, command.cancelReason());

        if (coffeeChat.isMenteeFlow()) {
            eventPublisher.publishMenteeCanceledFromMenteeFlowEvent(coffeeChat);
        } else {
            eventPublisher.publishMenteeCanceledFromMentorFlowEvent(coffeeChat);
        }
    }

    private CoffeeChatStatus determineCancelStatus(final CoffeeChat coffeeChat) {
        if (coffeeChat.isMenteeFlow()) {
            return CANCEL_FROM_MENTEE_FLOW;
        }
        return CANCEL_FROM_MENTOR_FLOW;
    }
}
