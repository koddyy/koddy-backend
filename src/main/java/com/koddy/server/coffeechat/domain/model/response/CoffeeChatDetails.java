package com.koddy.server.coffeechat.domain.model.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.global.utils.encrypt.Encryptor;

import java.time.LocalDateTime;

public record CoffeeChatDetails(
        long id,
        String status,
        String applyReason,
        String suggestReason,
        String question,
        String rejectReason,
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
                coffeeChat.getApplyReason(),
                coffeeChat.getSuggestReason(),
                (coffeeChat.getQuestion() != null) ? coffeeChat.getQuestion() : null,
                (coffeeChat.getRejectReason() != null) ? coffeeChat.getRejectReason() : null,
                (coffeeChat.getReservation() != null) ? coffeeChat.getReservation().getStart() : null,
                (coffeeChat.getReservation() != null) ? coffeeChat.getReservation().getEnd() : null,
                (coffeeChat.getStrategy() != null) ? coffeeChat.getStrategy().getType().getEng() : null,
                (coffeeChat.getStrategy() != null) ? encryptor.symmetricDecrypt(coffeeChat.getStrategy().getValue()) : null,
                coffeeChat.getCreatedAt(),
                coffeeChat.getLastModifiedAt()
        );
    }
}
