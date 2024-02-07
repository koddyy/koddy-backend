package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.response.QMenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.AppliedCoffeeChatQueryCondition;
import com.koddy.server.coffeechat.domain.repository.query.spec.SuggestedCoffeeChatQueryCondition;
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
import static com.koddy.server.member.domain.model.mentor.QMentor.mentor;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MenteeCoffeeChatScheduleQueryRepositoryImpl implements MenteeCoffeeChatScheduleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Slice<MenteeCoffeeChatScheduleData> fetchAppliedCoffeeChatsByCondition(
            final AppliedCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<MenteeCoffeeChatScheduleData> result = query
                .select(new QMenteeCoffeeChatScheduleData(
                        coffeeChat.id,
                        coffeeChat.status,
                        mentor.id,
                        mentor.name,
                        mentor.profileImageUrl,
                        mentor.universityProfile
                ))
                .from(coffeeChat)
                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.targetMemberId))
                .where(
                        coffeeChat.sourceMemberId.eq(condition.memberId()),
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
    public Slice<MenteeCoffeeChatScheduleData> fetchSuggestedCoffeeChatsByCondition(
            final SuggestedCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        final List<MenteeCoffeeChatScheduleData> result = query
                .select(new QMenteeCoffeeChatScheduleData(
                        coffeeChat.id,
                        coffeeChat.status,
                        mentor.id,
                        mentor.name,
                        mentor.profileImageUrl,
                        mentor.universityProfile
                ))
                .from(coffeeChat)
                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.sourceMemberId))
                .where(
                        coffeeChat.targetMemberId.eq(condition.memberId()),
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
