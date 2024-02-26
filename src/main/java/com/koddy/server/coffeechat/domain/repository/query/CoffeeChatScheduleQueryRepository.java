package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CoffeeChatScheduleQueryRepository {
    /**
     * 멘토 카테고리별 커피챗 개수
     */
    CoffeeChatCountPerCategory fetchMentorCoffeeChatCountPerCategory(final long mentorId);

    /**
     * 멘티 카테고리별 커피챗 개수
     */
    CoffeeChatCountPerCategory fetchMenteeCoffeeChatCountPerCategory(final long menteeId);

    /**
     * 멘토 내 일정
     */
    Slice<MentorCoffeeChatScheduleData> fetchMentorCoffeeChatSchedules(
            final MentorCoffeeChatQueryCondition condition,
            final Pageable pageable
    );

    /**
     * 멘티 내 일정
     */
    Slice<MenteeCoffeeChatScheduleData> fetchMenteeCoffeeChatSchedules(
            final MenteeCoffeeChatQueryCondition condition,
            final Pageable pageable
    );
}
