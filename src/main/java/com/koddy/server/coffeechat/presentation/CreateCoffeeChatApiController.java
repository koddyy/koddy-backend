package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.CreateCoffeeChatUseCase;
import com.koddy.server.coffeechat.application.usecase.command.MenteeApplyCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.MentorSuggestCoffeeChatCommand;
import com.koddy.server.coffeechat.presentation.dto.request.MenteeApplyCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.MentorSuggestCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.response.CreateCoffeeChatResponse;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTEE;
import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "4-2. 커피챗 신청/제안 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats")
public class CreateCoffeeChatApiController {
    private final CreateCoffeeChatUseCase createCoffeeChatUseCase;

    @Operation(summary = "멘토 -> 멘티 커피챗 제안 Endpoint")
    @PostMapping("/suggest/{menteeId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<CreateCoffeeChatResponse> suggestCoffeeChat(
            @Auth final Authenticated authenticated,
            @PathVariable final Long menteeId,
            @RequestBody @Valid final MentorSuggestCoffeeChatRequest request
    ) {
        final long coffeeChatId = createCoffeeChatUseCase.suggestCoffeeChat(new MentorSuggestCoffeeChatCommand(
                authenticated.id(),
                menteeId,
                request.applyReason()
        ));
        return ResponseEntity.ok(new CreateCoffeeChatResponse(coffeeChatId));
    }

    @Operation(summary = "멘티 -> 멘토 커피챗 신청 Endpoint")
    @PostMapping("/apply/{mentorId}")
    @AccessControl(role = MENTEE)
    public ResponseEntity<CreateCoffeeChatResponse> applyCoffeeChat(
            @Auth final Authenticated authenticated,
            @PathVariable final Long mentorId,
            @RequestBody @Valid final MenteeApplyCoffeeChatRequest request
    ) {
        final long coffeeChatId = createCoffeeChatUseCase.applyCoffeeChat(new MenteeApplyCoffeeChatCommand(
                authenticated.id(),
                mentorId,
                request.applyReason(),
                request.toReservationStart(),
                request.toReservationEnd()
        ));
        return ResponseEntity.ok(new CreateCoffeeChatResponse(coffeeChatId));
    }
}
