package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats;
import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats;
import com.koddy.server.coffeechat.application.usecase.query.response.CoffeeChatEachCategoryCounts;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.CoffeeChatScheduleQueryRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.query.PageCreator;
import com.koddy.server.global.query.SliceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@UseCase
public class GetCoffeeChatScheduleUseCase {
    private final CoffeeChatRepository coffeeChatRepository;
    private final CoffeeChatScheduleQueryRepository coffeeChatScheduleQueryRepository;

    public GetCoffeeChatScheduleUseCase(
            final CoffeeChatRepository coffeeChatRepository,
            final CoffeeChatScheduleQueryRepository coffeeChatScheduleQueryRepository
    ) {
        this.coffeeChatRepository = coffeeChatRepository;
        this.coffeeChatScheduleQueryRepository = coffeeChatScheduleQueryRepository;
    }

    public CoffeeChatEachCategoryCounts getEachCategoryCounts(final Authenticated authenticated) {
        if (authenticated.isMentor()) {
            return new CoffeeChatEachCategoryCounts(
                    coffeeChatRepository.getMentorWaitingCoffeeChatCount(authenticated.id),
                    coffeeChatRepository.getMentorSuggestedCoffeeChatCount(authenticated.id),
                    coffeeChatRepository.getMentorScheduledCoffeeChatCount(authenticated.id),
                    coffeeChatRepository.getMentorPassedCoffeeChatCount(authenticated.id)
            );
//            return CoffeeChatEachCategoryCounts.from(coffeeChatScheduleQueryRepository.fetchMentorCoffeeChatCountPerCategory(authenticated.id));
        }
        return new CoffeeChatEachCategoryCounts(
                coffeeChatRepository.getMenteeWaitingCoffeeChatCount(authenticated.id),
                coffeeChatRepository.getMenteeSuggestedCoffeeChatCount(authenticated.id),
                coffeeChatRepository.getMenteeScheduledCoffeeChatCount(authenticated.id),
                coffeeChatRepository.getMenteePassedCoffeeChatCount(authenticated.id)
        );
//        return CoffeeChatEachCategoryCounts.from(coffeeChatScheduleQueryRepository.fetchMenteeCoffeeChatCountPerCategory(authenticated.id));
    }

    public SliceResponse<List<MentorCoffeeChatScheduleData>> getMentorSchedules(final GetMentorCoffeeChats query) {
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(query.mentorId(), query.status());
        final Pageable pageable = PageCreator.create(query.page());

        final Slice<MentorCoffeeChatScheduleData> result = coffeeChatScheduleQueryRepository.fetchMentorCoffeeChatSchedules(condition, pageable);
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }

    public SliceResponse<List<MenteeCoffeeChatScheduleData>> getMenteeSchedules(final GetMenteeCoffeeChats query) {
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(query.menteeId(), query.status());
        final Pageable pageable = PageCreator.create(query.page());

        final Slice<MenteeCoffeeChatScheduleData> result = coffeeChatScheduleQueryRepository.fetchMenteeCoffeeChatSchedules(condition, pageable);
        return new SliceResponse<>(result.getContent(), result.hasNext());
    }
}
