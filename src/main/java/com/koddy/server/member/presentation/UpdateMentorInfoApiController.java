package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.member.application.usecase.UpdateMentorInfoUseCase;
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand;
import com.koddy.server.member.presentation.dto.request.UpdateMentorBasicInfoRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorScheduleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTOR;

@Tag(name = "멘토 정보 수정 API")
@RestController
@RequestMapping("/api/mentors/me")
@RequiredArgsConstructor
public class UpdateMentorInfoApiController {
    private final UpdateMentorInfoUseCase updateMentorInfoUseCase;

    @Operation(summary = "멘토 기본정보 수정 Endpoint")
    @PatchMapping("/basic-info")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> updateBasicInfo(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateMentorBasicInfoRequest request
    ) {
        updateMentorInfoUseCase.updateBasicInfo(new UpdateMentorBasicInfoCommand(
                authenticated.id(),
                request.name(),
                request.profileImageUrl(),
                request.introduction(),
                request.toLanguages(),
                request.school(),
                request.major(),
                request.enteredIn()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘토 스케줄 수정 Endpoint")
    @PatchMapping("/schedules")
    @AccessControl(role = MENTOR)
    public ResponseEntity<Void> updateSchedule(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateMentorScheduleRequest request
    ) {
        updateMentorInfoUseCase.updateSchedule(new UpdateMentorScheduleCommand(
                authenticated.id(),
                request.toPeriod(),
                request.toSchedules()
        ));
        return ResponseEntity.noContent().build();
    }
}
