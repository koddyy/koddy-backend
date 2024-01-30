package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats;
import com.koddy.server.coffeechat.domain.repository.query.MenteeCoffeeChatScheduleQueryRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
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
public class GetMenteeCoffeeChatScheduleUseCase {
    private final MenteeCoffeeChatScheduleQueryRepository menteeCoffeeChatScheduleQueryRepository;

    @KoddyReadOnlyTransactional
    public SliceResponse<List<MenteeCoffeeChatScheduleData>> getAppliedCoffeeChats(final GetMenteeCoffeeChats query) {
        final MenteeCoffeeChatQueryCondition condition = query.toCondition();
        final Pageable pageable = PageCreator.create(query.page());
        final Slice<MenteeCoffeeChatScheduleData> result = menteeCoffeeChatScheduleQueryRepository.fetchAppliedCoffeeChatsByCondition(condition, pageable);
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }

    @KoddyReadOnlyTransactional
    public SliceResponse<List<MenteeCoffeeChatScheduleData>> getSuggestedCoffeeChats(final GetMenteeCoffeeChats query) {
        final MenteeCoffeeChatQueryCondition condition = query.toCondition();
        final Pageable pageable = PageCreator.create(query.page());
        final Slice<MenteeCoffeeChatScheduleData> result = menteeCoffeeChatScheduleQueryRepository.fetchSuggestedCoffeeChatsByCondition(condition, pageable);
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }
}
