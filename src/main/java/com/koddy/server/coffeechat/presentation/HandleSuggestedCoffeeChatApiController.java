package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.HandleSuggestedCoffeeChatUseCase;
import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.presentation.dto.request.PendingSuggestedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.RejectSuggestedCoffeeChatRequest;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTEE;

@Tag(name = "4-4-2. 멘토가 제안한 커피챗 처리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats/suggested")
public class HandleSuggestedCoffeeChatApiController {
    private final HandleSuggestedCoffeeChatUseCase handleSuggestedCoffeeChatUseCase;

    @Operation(summary = "멘토가 제안한 커피챗 거절 Endpoint")
    @PatchMapping("/reject/{coffeeChatId}")
    @AccessControl(role = MENTEE)
    public ResponseEntity<Void> reject(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final RejectSuggestedCoffeeChatRequest request
    ) {
        handleSuggestedCoffeeChatUseCase.reject(new RejectSuggestedCoffeeChatCommand(coffeeChatId, request.rejectReason()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘토가 제안한 커피챗 1차 수락 Endpoint")
    @PatchMapping("/pending/{coffeeChatId}")
    @AccessControl(role = MENTEE)
    public ResponseEntity<Void> pending(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final PendingSuggestedCoffeeChatRequest request
    ) {
        handleSuggestedCoffeeChatUseCase.pending(new PendingSuggestedCoffeeChatCommand(
                coffeeChatId,
                request.question(),
                request.toReservationStart(),
                request.toReservationEnd()
        ));
        return ResponseEntity.noContent().build();
    }
}
