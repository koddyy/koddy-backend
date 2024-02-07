package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.PENDING_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.SUGGESTED_COFFEE_CHAT_NOT_FOUND;

public interface CoffeeChatRepository extends JpaRepository<CoffeeChat, Long> {
    default CoffeeChat getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new CoffeeChatException(COFFEE_CHAT_NOT_FOUND));
    }

    Optional<CoffeeChat> findByIdAndSourceMemberIdAndStatus(
            final Long id,
            final Long sourceMemberId,
            final CoffeeChatStatus status
    );

    Optional<CoffeeChat> findByIdAndTargetMemberIdAndStatus(
            final Long id,
            final Long targetMemberId,
            final CoffeeChatStatus status
    );

    /**
     * 멘토(Target) 기준 -> 멘티(Source)가 신청한 커피챗
     */
    default CoffeeChat getMenteeAppliedCoffeeChat(final Long id, final Long mentorId) {
        return findByIdAndTargetMemberIdAndStatus(id, mentorId, MENTEE_APPLY)
                .orElseThrow(() -> new CoffeeChatException(APPLIED_COFFEE_CHAT_NOT_FOUND));
    }

    /**
     * 멘토(Source) 기준 -> 멘토(Source)가 제안 & 멘티(Target)가 1차 수락한 커피챗
     */
    default CoffeeChat getMenteePendingCoffeeChat(final Long id, final Long mentorId) {
        return findByIdAndSourceMemberIdAndStatus(id, mentorId, MENTEE_PENDING)
                .orElseThrow(() -> new CoffeeChatException(PENDING_COFFEE_CHAT_NOT_FOUND));
    }

    /**
     * 멘티(Target) 기준 -> 멘토(Source)가 제안한 커피챗
     */
    default CoffeeChat getMentorSuggestedCoffeeChat(final Long id, final Long menteeId) {
        return findByIdAndTargetMemberIdAndStatus(id, menteeId, MENTOR_SUGGEST)
                .orElseThrow(() -> new CoffeeChatException(SUGGESTED_COFFEE_CHAT_NOT_FOUND));
    }

    Optional<CoffeeChat> findByIdAndSourceMemberIdAndStatusIn(
            final Long id,
            final Long sourceMemberId,
            final List<CoffeeChatStatus> status
    );

    default CoffeeChat getAppliedOrSuggestedCoffeeChat(final Long id, final Long sourceMemberId) {
        return findByIdAndSourceMemberIdAndStatusIn(id, sourceMemberId, List.of(MENTEE_APPLY, MENTOR_SUGGEST))
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
                findBySourceMemberIdAndStartYearAndStartMonthAndStatusIn(mentorId, year, month, List.of(MENTEE_PENDING, MENTOR_FINALLY_APPROVE));
        final List<CoffeeChat> appliedToMentor =
                findByTargetMemberIdAndStartYearAndStartMonthAndStatusIn(mentorId, year, month, List.of(MENTEE_APPLY, MENTOR_APPROVE));

        return Stream.concat(suggestedByMentor.stream(), appliedToMentor.stream())
                .sorted(Comparator.comparing(it -> it.getStart().toLocalDateTime()))
                .toList();
    }
}
