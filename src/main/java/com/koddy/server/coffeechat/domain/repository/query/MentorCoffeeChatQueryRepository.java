package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MentorCoffeeChatQueryRepository {
    /**
     * 제안한 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MentorCoffeeChatScheduleData> fetchSuggestedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 신청받은 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MentorCoffeeChatScheduleData> fetchAppliedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
