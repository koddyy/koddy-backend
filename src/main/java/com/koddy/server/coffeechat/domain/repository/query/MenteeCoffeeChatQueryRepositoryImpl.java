package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

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
        return null;
    }

    @Override
    public Slice<CoffeeChat> fetchSuggestedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    ) {
        return null;
    }
}
