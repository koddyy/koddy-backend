package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MenteeCoffeeChatQueryRepository {
    /**
     * 신청한 커피챗에 대한 상태별 정보 (멘티 -> 멘토)
     */
    Slice<CoffeeChat> fetchAppliedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 제안온 커피챗에 대한 상태별 정보 (멘토 -> 멘티)
     */
    Slice<CoffeeChat> fetchSuggestedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
