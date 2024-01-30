package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.global.PageResponse.hasNext;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MenteeCoffeeChatQueryRepositoryImpl implements MenteeCoffeeChatQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<CoffeeChat> fetchAppliedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        return projection(pageable, Arrays.asList(
                coffeeChat.sourceMemberId.eq(condition.menteeId()),
                statusEq(condition.status())
        ));
    }

    @Override
    public Slice<CoffeeChat> fetchSuggestedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        return projection(pageable, Arrays.asList(
                coffeeChat.targetMemberId.eq(condition.menteeId()),
                statusEq(condition.status())
        ));
    }

    private Slice<CoffeeChat> projection(
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final List<CoffeeChat> result = query
                .select(coffeeChat)
                .from(coffeeChat)
                .where(whereConditions.toArray(Predicate[]::new))
                .orderBy(coffeeChat.lastModifiedAt.desc(), coffeeChat.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long totalCount = query
                .select(coffeeChat.id.count())
                .from(coffeeChat)
                .where(whereConditions.toArray(Predicate[]::new))
                .fetchOne();

        return new SliceImpl<>(
                result,
                pageable,
                hasNext(pageable, result.size(), totalCount)
        );
    }

    private BooleanExpression statusEq(final CoffeeChatStatus status) {
        if (status == null) {
            return null;
        }
        return coffeeChat.status.eq(status);
    }
}
