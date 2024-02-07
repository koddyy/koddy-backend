package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.AppliedCoffeeChatQueryCondition;
import com.koddy.server.coffeechat.domain.repository.query.spec.SuggestedCoffeeChatQueryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MenteeCoffeeChatScheduleQueryRepository {
    /**
     * 신청한 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MenteeCoffeeChatScheduleData> fetchAppliedCoffeeChatsByCondition(
            final AppliedCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 제안받은 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MenteeCoffeeChatScheduleData> fetchSuggestedCoffeeChatsByCondition(
            final SuggestedCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
