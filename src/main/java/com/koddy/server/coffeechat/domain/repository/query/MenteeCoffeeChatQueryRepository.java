package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MenteeCoffeeChatQueryRepository {
    /**
     * 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보
     */
    Slice<Mentor> fetchAppliedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 제안받은 커피챗에 대한 상태별 리스트에 포함된 멘토 정보
     */
    Slice<Mentor> fetchSuggestedCoffeeChatsByCondition(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
