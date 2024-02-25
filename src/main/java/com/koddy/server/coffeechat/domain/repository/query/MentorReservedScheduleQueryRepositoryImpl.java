package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;

@Repository
@KoddyReadOnlyTransactional
public class MentorReservedScheduleQueryRepositoryImpl implements MentorReservedScheduleQueryRepository {
    private final JPAQueryFactory query;

    public MentorReservedScheduleQueryRepositoryImpl(final JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public List<CoffeeChat> fetchReservedCoffeeChat(final long mentorId, final int year, final int month) {
        final List<CoffeeChat> suggestedByMentor = query
                .select(coffeeChat)
                .from(coffeeChat)
                .where(
                        coffeeChat.mentorId.eq(mentorId),
                        reservationStartBetween(year, month),
                        statusIn(List.of(MENTEE_PENDING, MENTOR_FINALLY_APPROVE))
                )
                .fetch();

        final List<CoffeeChat> appliedToMentor = query
                .select(coffeeChat)
                .from(coffeeChat)
                .where(
                        coffeeChat.mentorId.eq(mentorId),
                        reservationStartBetween(year, month),
                        statusIn(List.of(MENTEE_APPLY, MENTOR_APPROVE))
                )
                .fetch();

        return Stream.concat(suggestedByMentor.stream(), appliedToMentor.stream())
                .sorted(Comparator.comparing(it -> it.getReservation().getStart()))
                .toList();
    }

    private BooleanExpression statusIn(final List<CoffeeChatStatus> status) {
        if (CollectionUtils.isEmpty(status)) {
            return null;
        }
        return coffeeChat.status.in(status);
    }

    private BooleanExpression reservationStartBetween(final int year, final int month) {
        final LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        final LocalDateTime end = start.plusMonths(1);

        return coffeeChat.reservation.start.goe(start)
                .and(coffeeChat.reservation.start.lt(end));
    }
}
