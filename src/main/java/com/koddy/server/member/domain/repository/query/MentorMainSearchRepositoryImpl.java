package com.koddy.server.member.domain.repository.query;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.global.PageResponse.hasNext;
import static com.koddy.server.member.domain.model.QAvailableLanguage.availableLanguage;
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
    public Slice<Mentee> fetchMenteesByCondition(final SearchMenteeCondition condition, final Pageable pageable) {
        final List<Long> filteringMenteeIds = filteringByCondition(condition);

        final List<Mentee> result = query
                .select(mentee)
                .from(mentee)
                .where(mentee.id.in(filteringMenteeIds))
                .orderBy(mentee.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long totalCount = query
                .select(mentee.id.count())
                .from(mentee)
                .where(mentee.id.in(filteringMenteeIds))
                .where()
                .fetchOne();

        return new SliceImpl<>(
                result,
                pageable,
                hasNext(pageable, result.size(), totalCount)
        );
    }

    private List<Long> filteringByCondition(final SearchMenteeCondition condition) {
        final List<Long> containsNationalityMenteeIds = query
                .select(mentee.id)
                .from(mentee)
                .where(filteringNationality(condition.nationality()))
                .orderBy(mentee.id.desc())
                .fetch();

        final List<Long> containsLanguageMenteeIds = filteringLanguage(condition.language());

        return compactMenteeIds(containsNationalityMenteeIds, containsLanguageMenteeIds);
    }

    private Predicate filteringNationality(final SearchMenteeCondition.NationalityCondition nationality) {
        if (nationality.contains()) {
            final BooleanBuilder builder = new BooleanBuilder();
            for (final Nationality value : nationality.values()) {
                builder.or(mentee.nationality.eq(value));
            }
            return builder;
        }
        return null;
    }

    private List<Long> filteringLanguage(final SearchMenteeCondition.LanguageCondition language) {
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

    private List<Long> compactMenteeIds(
            final List<Long> containsNationalityMenteeIds,
            final List<Long> containsLanguageMenteeIds
    ) {
        final List<Long> commonMenteeIds = new ArrayList<>(containsNationalityMenteeIds);
        commonMenteeIds.retainAll(containsLanguageMenteeIds);
        return commonMenteeIds;
    }
}
