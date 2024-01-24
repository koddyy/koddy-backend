package com.koddy.server.member.domain.repository.query;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.query.spec.SearchMentee;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.member.domain.model.mentee.QMentee.mentee;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MentorMainSearchRepositoryImpl implements MentorMainSearchRepository {
    private final JPAQueryFactory query;

    @Override
    public List<Mentee> fetchAppliedMentees(final long mentorId, final int limit) {
        return query
                .select(mentee)
                .from(coffeeChat)
                .innerJoin(mentee).on(mentee.id.eq(coffeeChat.sourceMemberId))
                .where(
                        coffeeChat.targetMemberId.eq(mentorId),
                        coffeeChat.status.eq(APPLY)
                )
                .limit(limit)
                .orderBy(coffeeChat.id.desc())
                .fetch();
    }

    @Override
    public Slice<Mentee> fetchMentees(final SearchMentee search, final Pageable pageable) {
        return null;
    }
}
