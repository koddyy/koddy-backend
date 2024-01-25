package com.koddy.server.member.domain.repository.query;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.member.domain.model.QAvailableLanguage.availableLanguage;
import static com.koddy.server.member.domain.model.mentor.QMentor.mentor;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MenteeMainSearchRepositoryImpl implements MenteeMainSearchRepository {
    private final JPAQueryFactory query;

    @Override
    public List<Mentor> fetchSuggestedMentors(final long menteeId, final int limit) {
        return query
                .select(mentor)
                .from(coffeeChat)
                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.sourceMemberId))
                .where(
                        coffeeChat.targetMemberId.eq(menteeId),
                        coffeeChat.status.eq(APPLY)
                )
                .limit(limit)
                .orderBy(coffeeChat.id.desc())
                .fetch();
    }

    @Override
    public Slice<Mentor> fetchMentorsByCondition(final SearchMentorCondition condition, final Pageable pageable) {
        final List<Long> filteringMentorIds = filteringByCondition(condition);

        final List<Mentor> result = query
                .select(mentor)
                .from(mentor)
                .where(mentor.id.in(filteringMentorIds))
                .orderBy(mentor.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long totalCount = query
                .select(mentor.id.count())
                .from(mentor)
                .where(mentor.id.in(filteringMentorIds))
                .where()
                .fetchOne();

        return new SliceImpl<>(
                result,
                pageable,
                hasNext(pageable, result.size(), totalCount)
        );
    }

    private List<Long> filteringByCondition(final SearchMentorCondition condition) {
        return filteringLanguage(condition.language());
    }

    private List<Long> filteringLanguage(final SearchMentorCondition.LanguageCondition language) {
        if (language.contains()) {
            return query
                    .selectDistinct(availableLanguage.member.id)
                    .from(availableLanguage)
                    .where(availableLanguage.language.category.in(language.values()))
                    .groupBy(availableLanguage.member.id)
                    .having(availableLanguage.language.category.count().goe(language.values().size()))
                    .orderBy(availableLanguage.member.id.desc())
                    .fetch();
        }
        return query
                .selectDistinct(availableLanguage.member.id)
                .from(availableLanguage)
                .orderBy(availableLanguage.member.id.desc())
                .fetch();
    }

    private boolean hasNext(
            final Pageable pageable,
            final int contentSize,
            final Long totalCount
    ) {
        if (contentSize == pageable.getPageSize()) {
            return (long) contentSize * (pageable.getPageNumber() + 1) != totalCount;
        }
        return false;
    }
}
