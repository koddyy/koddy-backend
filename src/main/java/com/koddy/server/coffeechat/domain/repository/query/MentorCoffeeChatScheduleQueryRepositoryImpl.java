package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.response.QMentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.member.domain.model.mentee.QMentee.mentee;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MentorCoffeeChatScheduleQueryRepositoryImpl implements MentorCoffeeChatScheduleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<MentorCoffeeChatScheduleData> fetchSuggestedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<MentorCoffeeChatScheduleData> result = query
                .select(new QMentorCoffeeChatScheduleData(
                        coffeeChat.id,
                        coffeeChat.status,
                        mentee.id,
                        mentee.name,
                        mentee.profileImageUrl,
                        mentee.interest
                ))
                .from(coffeeChat)
                .innerJoin(mentee).on(mentee.id.eq(coffeeChat.targetMemberId))
                .where(
                        coffeeChat.sourceMemberId.eq(condition.mentorId()),
                        statusIn(condition.status())
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
    public Slice<MentorCoffeeChatScheduleData> fetchAppliedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<MentorCoffeeChatScheduleData> result = query
                .select(new QMentorCoffeeChatScheduleData(
                        coffeeChat.id,
                        coffeeChat.status,
                        mentee.id,
                        mentee.name,
                        mentee.profileImageUrl,
                        mentee.interest
                ))
                .from(coffeeChat)
                .innerJoin(mentee).on(mentee.id.eq(coffeeChat.sourceMemberId))
                .where(
                        coffeeChat.targetMemberId.eq(condition.mentorId()),
                        statusIn(condition.status())
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

    private BooleanExpression statusIn(final List<CoffeeChatStatus> status) {
        if (CollectionUtils.isEmpty(status)) {
            return null;
        }
        return coffeeChat.status.in(status);
    }
}
