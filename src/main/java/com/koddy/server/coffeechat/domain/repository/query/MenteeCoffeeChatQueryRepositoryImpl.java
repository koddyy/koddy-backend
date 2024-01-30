package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.member.domain.model.mentor.QMentor.mentor;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MenteeCoffeeChatQueryRepositoryImpl implements MenteeCoffeeChatQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<Mentor> fetchAppliedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<Mentor> result = query
                .select(mentor)
                .from(coffeeChat)
                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.targetMemberId))
                .where(
                        coffeeChat.sourceMemberId.eq(condition.menteeId()),
                        statusEq(condition.status())
                )
                .orderBy(coffeeChat.lastModifiedAt.desc(), coffeeChat.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(
                result.stream().limit(pageable.getPageSize()).toList(),
                pageable,
                result.size() > pageable.getPageSize()
        );
    }

    @Override
    public Slice<Mentor> fetchSuggestedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<Mentor> result = query
                .select(mentor)
                .from(coffeeChat)
                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.sourceMemberId))
                .where(
                        coffeeChat.targetMemberId.eq(condition.menteeId()),
                        statusEq(condition.status())
                )
                .orderBy(coffeeChat.lastModifiedAt.desc(), coffeeChat.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(
                result.stream().limit(pageable.getPageSize()).toList(),
                pageable,
                result.size() > pageable.getPageSize()
        );
    }

    private BooleanExpression statusEq(final CoffeeChatStatus status) {
        if (status == null) {
            return null;
        }
        return coffeeChat.status.eq(status);
    }
}
