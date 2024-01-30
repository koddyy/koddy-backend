package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MenteeCoffeeChatQueryRepository {
    /**
     * 신청한 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MenteeCoffeeChatScheduleData> fetchAppliedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 제안받은 커피챗에 대한 상태별 리스트 정보
     */
    Slice<MenteeCoffeeChatScheduleData> fetchSuggestedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
