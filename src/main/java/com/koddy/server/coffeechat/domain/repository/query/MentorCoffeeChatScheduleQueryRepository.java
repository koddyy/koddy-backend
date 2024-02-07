package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.AppliedCoffeeChatQueryCondition;
import com.koddy.server.coffeechat.domain.repository.query.spec.SuggestedCoffeeChatQueryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MentorCoffeeChatScheduleQueryRepository {
    /**
     * 제안한 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MentorCoffeeChatScheduleData> fetchSuggestedCoffeeChatsByCondition(
            final SuggestedCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 신청받은 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MentorCoffeeChatScheduleData> fetchAppliedCoffeeChatsByCondition(
            final AppliedCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
