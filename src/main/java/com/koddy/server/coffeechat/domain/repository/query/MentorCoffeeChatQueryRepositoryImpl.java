package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.member.domain.model.mentee.QMentee.mentee;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MentorCoffeeChatQueryRepositoryImpl implements MentorCoffeeChatQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<Mentee> fetchSuggestedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<Mentee> result = query
                .select(mentee)
                .from(coffeeChat)
                .innerJoin(mentee).on(mentee.id.eq(coffeeChat.targetMemberId))
                .where(
                        coffeeChat.sourceMemberId.eq(condition.mentorId()),
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
    public Slice<Mentee> fetchAppliedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<Mentee> result = query
                .select(mentee)
                .from(coffeeChat)
                .innerJoin(mentee).on(mentee.id.eq(coffeeChat.sourceMemberId))
                .where(
                        coffeeChat.targetMemberId.eq(condition.mentorId()),
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
