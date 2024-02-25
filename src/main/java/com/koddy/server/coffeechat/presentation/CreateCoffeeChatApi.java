package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.CreateCoffeeChatUseCase;
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatByApplyCommand;
import com.koddy.server.coffeechat.application.usecase.command.CreateCoffeeChatBySuggestCommand;
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatByApplyRequest;
import com.koddy.server.coffeechat.presentation.request.CreateCoffeeChatBySuggestRequest;
import com.koddy.server.global.ResponseWrapper;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTEE;
import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "4-2. 커피챗 신청/제안 API")
@RestController
@RequestMapping("/api/coffeechats")
public class CreateCoffeeChatApi {
    private final CreateCoffeeChatUseCase createCoffeeChatUseCase;

    public CreateCoffeeChatApi(final CreateCoffeeChatUseCase createCoffeeChatUseCase) {
        this.createCoffeeChatUseCase = createCoffeeChatUseCase;
    }

    @Operation(summary = "멘티 -> 멘토 커피챗 신청 Endpoint")
    @PostMapping("/apply")
    @AccessControl(role = MENTEE)
    public ResponseEntity<ResponseWrapper<Long>> createByApply(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final CreateCoffeeChatByApplyRequest request
    ) {
        final long coffeeChatId = createCoffeeChatUseCase.createByApply(new CreateCoffeeChatByApplyCommand(
                authenticated.id,
                request.mentorId(),
                request.applyReason(),
                request.toReservation()
        ));
        return ResponseEntity.ok(new ResponseWrapper<>(coffeeChatId));
    }

    @Operation(summary = "멘토 -> 멘티 커피챗 제안 Endpoint")
    @PostMapping("/suggest")
    @AccessControl(role = MENTOR)
    public ResponseEntity<ResponseWrapper<Long>> createBySuggest(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final CreateCoffeeChatBySuggestRequest request
    ) {
        final long coffeeChatId = createCoffeeChatUseCase.createBySuggest(new CreateCoffeeChatBySuggestCommand(
                authenticated.id,
                request.menteeId(),
                request.suggestReason()
        ));
        return ResponseEntity.ok(new ResponseWrapper<>(coffeeChatId));
    }
}
