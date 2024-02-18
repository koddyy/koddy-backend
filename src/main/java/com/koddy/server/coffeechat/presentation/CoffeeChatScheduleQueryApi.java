package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleUseCase;
import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats;
import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats;
import com.koddy.server.coffeechat.application.usecase.query.response.CoffeeChatEachCategoryCounts;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.presentation.request.GetCoffeeChatScheduleRequest;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.query.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "4-5. 내 일정 관련 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats")
public class CoffeeChatScheduleQueryApi {
    private final GetCoffeeChatScheduleUseCase getCoffeeChatScheduleUseCase;

    @Operation(summary = "내 일정 상태별 커피챗 개수 조회")
    @GetMapping("/me/category-counts")
    public ResponseEntity<CoffeeChatEachCategoryCounts> getEachCategoryCounts(
            @Auth final Authenticated authenticated
    ) {
        final CoffeeChatEachCategoryCounts result = getCoffeeChatScheduleUseCase.getEachCategoryCounts(authenticated);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "내 일정 상태별 커피챗 정보 조회")
    @GetMapping("/me/schedules")
    public ResponseEntity<SliceResponse<?>> getSchedules(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetCoffeeChatScheduleRequest request
    ) {
        final SliceResponse<?> result = authenticated.isMentor()
                ? getMentorSchedules(authenticated, request)
                : getMenteeSchedules(authenticated, request);

        return ResponseEntity.ok(result);
    }

    private SliceResponse<List<MentorCoffeeChatScheduleData>> getMentorSchedules(
            final Authenticated authenticated,
            final GetCoffeeChatScheduleRequest request
    ) {
        return getCoffeeChatScheduleUseCase.getMentorSchedules(new GetMentorCoffeeChats(
                authenticated.id(),
                request.convertToCoffeeChatStatus(),
                request.page()
        ));
    }

    private SliceResponse<List<MenteeCoffeeChatScheduleData>> getMenteeSchedules(
            final Authenticated authenticated,
            final GetCoffeeChatScheduleRequest request
    ) {
        return getCoffeeChatScheduleUseCase.getMenteeSchedules(new GetMenteeCoffeeChats(
                authenticated.id(),
                request.convertToCoffeeChatStatus(),
                request.page()
        ));
    }
}
