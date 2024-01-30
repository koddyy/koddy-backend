package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import com.koddy.server.member.domain.model.mentee.Mentee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MentorCoffeeChatQueryRepository {
    /**
     * 제안한 커피챗에 대한 상태별 리스트에 포함된 멘티 정보
     */
    Slice<Mentee> fetchSuggestedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 신청받은 커피챗에 대한 상태별 리스트에 포함된 멘티 정보
     */
    Slice<Mentee> fetchAppliedCoffeeChatsByCondition(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
