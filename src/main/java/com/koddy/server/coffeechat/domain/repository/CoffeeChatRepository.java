package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_COFFEE_CHAT_NOT_FOUND;

public interface CoffeeChatRepository extends JpaRepository<CoffeeChat, Long> {
    Optional<CoffeeChat> findByIdAndStatus(final Long id, final CoffeeChatStatus status);

    default CoffeeChat getAppliedCoffeeChat(final Long id) {
        return findByIdAndStatus(id, APPLY)
                .orElseThrow(() -> new CoffeeChatException(APPLIED_COFFEE_CHAT_NOT_FOUND));
    }
}
