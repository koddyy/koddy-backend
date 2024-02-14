package com.koddy.server.member.domain.repository.query;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.query.response.QSuggestedCoffeeChatsByMentor;
import com.koddy.server.member.domain.repository.query.response.SuggestedCoffeeChatsByMentor;
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
import static com.koddy.server.member.domain.model.QAvailableLanguage.availableLanguage;
import static com.koddy.server.member.domain.model.mentor.QMentor.mentor;

@Repository
@KoddyReadOnlyTransactional
@RequiredArgsConstructor
public class MenteeMainSearchRepositoryImpl implements MenteeMainSearchRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<SuggestedCoffeeChatsByMentor> fetchSuggestedMentors(final long menteeId, final int limit) {
        final List<SuggestedCoffeeChatsByMentor> result = query
                .select(new QSuggestedCoffeeChatsByMentor(
                        coffeeChat.id,
                        mentor.id,
                        mentor.name,
                        mentor.profileImageUrl,
                        mentor.universityProfile
                ))
                .from(coffeeChat)
                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.mentorId))
                .where(
                        coffeeChat.menteeId.eq(menteeId),
                        coffeeChat.status.eq(MENTOR_SUGGEST)
                )
                .limit(limit)
                .orderBy(coffeeChat.id.desc())
                .fetch();

        final Long totalCount = query
                .select(coffeeChat.id.count())
                .from(coffeeChat)
                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.mentorId))
                .where(
                        coffeeChat.menteeId.eq(menteeId),
                        coffeeChat.status.eq(MENTOR_SUGGEST)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, PageRequest.of(0, limit), () -> totalCount);
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
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(
                result.stream().limit(pageable.getPageSize()).toList(),
                pageable,
                result.size() > pageable.getPageSize()
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
}
