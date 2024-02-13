package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.COFFEE_CHAT_NOT_FOUND;

public interface CoffeeChatRepository extends JpaRepository<CoffeeChat, Long> {
    default CoffeeChat getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_FOUND));
    }

    Optional<CoffeeChat> findByIdAndMentorId(final Long id, final Long mentorId);

    default CoffeeChat getByIdAndMentorId(final Long id, final Long mentorId) {
        return findByIdAndMentorId(id, mentorId)
                .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_FOUND));
    }

    Optional<CoffeeChat> findByIdAndMenteeId(final Long id, final Long menteeId);

    default CoffeeChat getByIdAndMenteeId(final Long id, final Long menteeId) {
        return findByIdAndMenteeId(id, menteeId)
                .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_FOUND));
    }
}
