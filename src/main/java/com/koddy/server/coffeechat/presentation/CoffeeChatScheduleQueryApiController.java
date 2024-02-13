package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleUseCase;
import com.koddy.server.coffeechat.application.usecase.query.GetMenteeCoffeeChats;
import com.koddy.server.coffeechat.application.usecase.query.GetMentorCoffeeChats;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.presentation.dto.request.GetCoffeeChatScheduleRequest;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
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

import static com.koddy.server.member.domain.model.Role.MENTEE;
import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "4-5. 내 일정 커피챗 상태별 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats")
public class CoffeeChatScheduleQueryApiController {
    private final GetCoffeeChatScheduleUseCase getCoffeeChatScheduleUseCase;

    @Operation(summary = "멘토 내 일정 커피챗 상태별 조회")
    @GetMapping("/mentors/me")
    @AccessControl(role = MENTOR)
    public ResponseEntity<SliceResponse<List<MentorCoffeeChatScheduleData>>> getMentorSchedules(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetCoffeeChatScheduleRequest request
    ) {
        final SliceResponse<List<MentorCoffeeChatScheduleData>> result = getCoffeeChatScheduleUseCase.getMentorSchedules(new GetMentorCoffeeChats(
                authenticated.id(),
                request.convertToCoffeeChatStatus(),
                request.page()
        ));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "멘티 내 일정 커피챗 상태별 조회")
    @GetMapping("/mentees/me")
    @AccessControl(role = MENTEE)
    public ResponseEntity<SliceResponse<List<MenteeCoffeeChatScheduleData>>> getMenteeSchedules(
            @Auth final Authenticated authenticated,
            @ModelAttribute @Valid final GetCoffeeChatScheduleRequest request
    ) {
        final SliceResponse<List<MenteeCoffeeChatScheduleData>> result = getCoffeeChatScheduleUseCase.getMenteeSchedules(new GetMenteeCoffeeChats(
                authenticated.id(),
                request.convertToCoffeeChatStatus(),
                request.page()
        ));
        return ResponseEntity.ok(result);
    }
}
