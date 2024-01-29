package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MentorCoffeeChatQueryRepository {
    /**
     * 제안한 커피챗에 대한 상태별 정보 (멘토 -> 멘티)
     */
    Slice<CoffeeChat> fetchSuggestedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 신청온 커피챗에 대한 상태별 정보 (멘티 -> 멘토)
     */
    Slice<CoffeeChat> fetchAppliedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
