package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.ApproveCoffeeChatUseCase;
import com.koddy.server.coffeechat.application.usecase.command.ApproveMenteeApplyCommand;
import com.koddy.server.coffeechat.application.usecase.command.ApproveMentorSuggestCommand;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.presentation.dto.request.ApproveMenteeApplyRequest;
import com.koddy.server.coffeechat.presentation.dto.request.ApproveMentorSuggestRequest;
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
import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "4-3. 커피챗 수락 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffeechats/approve")
public class ApproveCoffeeChatApiController {
    private final ApproveCoffeeChatUseCase approveCoffeeChatUseCase;

    @Operation(summary = "멘토의 커피챗 제안 수락 Endpoint -> 멘티 API")
    @PatchMapping("/suggested/{coffeeChatId}")
    @AccessControl(role = MENTEE)
    public ResponseEntity<Void> suggestByMentor(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final ApproveMentorSuggestRequest request
    ) {
        approveCoffeeChatUseCase.suggestByMentor(new ApproveMentorSuggestCommand(
                coffeeChatId,
                new Reservation(request.start()),
                new Reservation(request.end())
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘티의 커피챗 신청 수락 Endpoint -> 멘토 API")
    @PatchMapping("/applied/{coffeeChatId}")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> applyByMentee(
            @Auth final Authenticated authenticated,
            @PathVariable final Long coffeeChatId,
            @RequestBody @Valid final ApproveMenteeApplyRequest request
    ) {
        approveCoffeeChatUseCase.applyByMentee(new ApproveMenteeApplyCommand(
                coffeeChatId,
                Strategy.Type.from(request.chatType()),
                request.chatValue()
        ));
        return ResponseEntity.noContent().build();
    }
}
