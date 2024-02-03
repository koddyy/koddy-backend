package com.koddy.server.coffeechat.domain.model.response;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.global.utils.encrypt.Encryptor;

import java.time.LocalDateTime;

public record CoffeeChatDetails(
        long id,
        String status,
        String applyReason,
        String question,
        String rejectReason,
        LocalDateTime start,
        LocalDateTime end,
        String chatType,
        String chatValue
) {
    public static CoffeeChatDetails of(final CoffeeChat coffeeChat, final Encryptor encryptor) {
        return new CoffeeChatDetails(
                coffeeChat.getId(),
                coffeeChat.getStatus().getValue(),
                coffeeChat.getApplyReason(),
                (coffeeChat.getQuestion() != null) ? coffeeChat.getQuestion() : null,
                (coffeeChat.getRejectReason() != null) ? coffeeChat.getRejectReason() : null,
                (coffeeChat.getStart() != null) ? coffeeChat.getStart().toLocalDateTime() : null,
                (coffeeChat.getEnd() != null) ? coffeeChat.getEnd().toLocalDateTime() : null,
                (coffeeChat.getStrategy() != null) ? coffeeChat.getStrategy().getType().getEng() : null,
                (coffeeChat.getStrategy() != null) ? encryptor.symmetricDecrypt(coffeeChat.getStrategy().getValue()) : null
        );
    }
}
