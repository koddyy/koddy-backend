package com.koddy.server.coffeechat.domain.model.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.global.utils.encrypt.Encryptor;

import java.time.LocalDateTime;

public record CoffeeChatDetails(
        long id,
        String status,
        String applyReason,
        String suggestReason,
        String cancelReason,
        String rejectReason,
        String question,
        LocalDateTime start,
        LocalDateTime end,
        String chatType,
        String chatValue,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
    public static CoffeeChatDetails of(final CoffeeChat coffeeChat, final Encryptor encryptor) {
        return new CoffeeChatDetails(
                coffeeChat.getId(),
                coffeeChat.getStatus().name(),
                coffeeChat.getReason().getApplyReason(),
                coffeeChat.getReason().getSuggestReason(),
                coffeeChat.getReason().getCancelReason(),
                coffeeChat.getReason().getRejectReason(),
                (coffeeChat.getQuestion() != null) ? coffeeChat.getQuestion() : null,
                (coffeeChat.getReservation() != null) ? coffeeChat.getReservation().getStart() : null,
                (coffeeChat.getReservation() != null) ? coffeeChat.getReservation().getEnd() : null,
                (coffeeChat.getStrategy() != null) ? coffeeChat.getStrategy().getType().getEng() : null,
                (coffeeChat.getStrategy() != null) ? encryptor.decrypt(coffeeChat.getStrategy().getValue()) : null,
                coffeeChat.getCreatedAt(),
                coffeeChat.getLastModifiedAt()
        );
    }
}
