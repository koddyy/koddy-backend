package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.SUGGEST;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.PENDING_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.SUGGESTED_COFFEE_CHAT_NOT_FOUND;

public interface CoffeeChatRepository extends JpaRepository<CoffeeChat, Long> {
    Optional<CoffeeChat> findByIdAndStatus(final Long id, final CoffeeChatStatus status);

    default CoffeeChat getAppliedCoffeeChat(final Long id) {
        return findByIdAndStatus(id, APPLY)
                .orElseThrow(() -> new CoffeeChatException(APPLIED_COFFEE_CHAT_NOT_FOUND));
    }

    default CoffeeChat getSuggestedCoffeeChat(final Long id) {
        return findByIdAndStatus(id, SUGGEST)
                .orElseThrow(() -> new CoffeeChatException(SUGGESTED_COFFEE_CHAT_NOT_FOUND));
    }

    default CoffeeChat getPendingCoffeeChat(final Long id) {
        return findByIdAndStatus(id, PENDING)
                .orElseThrow(() -> new CoffeeChatException(PENDING_COFFEE_CHAT_NOT_FOUND));
    }

    Optional<CoffeeChat> findByIdAndSourceMemberIdAndStatusIn(
            final Long id,
            final Long sourceMemberId,
            final List<CoffeeChatStatus> status
    );

    default CoffeeChat getAppliedOrSuggestedCoffeeChat(final Long id, final Long sourceMemberId) {
        return findByIdAndSourceMemberIdAndStatusIn(id, sourceMemberId, List.of(APPLY, SUGGEST))
                .orElseThrow(() -> new CoffeeChatException(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND));
    }

    List<CoffeeChat> findBySourceMemberIdAndStartYearAndStartMonthAndStatusIn(
            final long sourceMemberId,
            final int year,
            final int month,
            final List<CoffeeChatStatus> status
    );

    List<CoffeeChat> findByTargetMemberIdAndStartYearAndStartMonthAndStatusIn(
            final long targetMemberId,
            final int year,
            final int month,
            final List<CoffeeChatStatus> status
    );

    default List<CoffeeChat> getReservedCoffeeChat(final long mentorId, final int year, final int month) {
        final List<CoffeeChat> suggestedByMentor =
                findBySourceMemberIdAndStartYearAndStartMonthAndStatusIn(mentorId, year, month, List.of(PENDING, APPROVE));
        final List<CoffeeChat> appliedToMentor =
                findByTargetMemberIdAndStartYearAndStartMonthAndStatusIn(mentorId, year, month, List.of(APPLY, APPROVE));

        return Stream.concat(suggestedByMentor.stream(), appliedToMentor.stream())
                .sorted(Comparator.comparing(it -> it.getStart().toLocalDateTime()))
                .toList();
    }
}
