package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.HandlePendingCoffeeChatUseCase;
import com.koddy.server.coffeechat.presentation.request.FinallyApprovePendingCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.request.FinallyCancelPendingCoffeeChatRequest;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "4-4-3. Pending 상태인 커피챗에 대한 멘토의 최종 결정")
@RestController
@RequestMapping("/api/coffeechats/pending")
public class HandlePendingCoffeeChatApi {
    private final HandlePendingCoffeeChatUseCase handlePendingCoffeeChatUseCase;

    public HandlePendingCoffeeChatApi(final HandlePendingCoffeeChatUseCase handlePendingCoffeeChatUseCase) {
        this.handlePendingCoffeeChatUseCase = handlePendingCoffeeChatUseCase;
    }

    @Operation(summary = "Pending 상태 커피챗 최종 취소 Endpoint")
    @PatchMapping("/cancel/{coffeeChatId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> finallyCancel(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final FinallyCancelPendingCoffeeChatRequest request
    ) {
        handlePendingCoffeeChatUseCase.finallyCancel(request.toCommand(authenticated.id, coffeeChatId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pending 상태 커피챗 최종 수락 Endpoint")
    @PatchMapping("/approve/{coffeeChatId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> finallyApprove(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final FinallyApprovePendingCoffeeChatRequest request
    ) {
        handlePendingCoffeeChatUseCase.finallyApprove(request.toCommand(authenticated.id, coffeeChatId));
        return ResponseEntity.noContent().build();
    }
}
