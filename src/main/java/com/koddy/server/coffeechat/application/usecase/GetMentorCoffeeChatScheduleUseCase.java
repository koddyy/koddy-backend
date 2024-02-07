package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats;
import com.koddy.server.coffeechat.domain.repository.query.MentorCoffeeChatScheduleQueryRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.AppliedCoffeeChatQueryCondition;
import com.koddy.server.coffeechat.domain.repository.query.spec.SuggestedCoffeeChatQueryCondition;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.query.PageCreator;
import com.koddy.server.global.query.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GetMentorCoffeeChatScheduleUseCase {
    private final MentorCoffeeChatScheduleQueryRepository mentorCoffeeChatScheduleQueryRepository;

    @KoddyReadOnlyTransactional
    public SliceResponse<List<MentorCoffeeChatScheduleData>> getSuggestedCoffeeChats(final GetMentorCoffeeChats query) {
        final SuggestedCoffeeChatQueryCondition condition = new SuggestedCoffeeChatQueryCondition(query.mentorId(), query.status());
        final Pageable pageable = PageCreator.create(query.page());
        final Slice<MentorCoffeeChatScheduleData> result = mentorCoffeeChatScheduleQueryRepository.fetchSuggestedCoffeeChatsByCondition(condition, pageable);
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }

    @KoddyReadOnlyTransactional
    public SliceResponse<List<MentorCoffeeChatScheduleData>> getAppliedCoffeeChats(final GetMentorCoffeeChats query) {
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(query.mentorId(), query.status());
        final Pageable pageable = PageCreator.create(query.page());
        final Slice<MentorCoffeeChatScheduleData> result = mentorCoffeeChatScheduleQueryRepository.fetchAppliedCoffeeChatsByCondition(condition, pageable);
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }
}
