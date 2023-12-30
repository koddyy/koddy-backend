package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.OnlyMentor;
import com.koddy.server.member.application.usecase.UpdateMentorInfoUseCase;
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorPasswordCommand;
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand;
import com.koddy.server.member.presentation.dto.request.UpdateMentorBasicInfoRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorPasswordRequest;
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

@Tag(name = "멘토 정보 수정 API")
@RestController
@RequestMapping("/api/mentors/me")
@RequiredArgsConstructor
@OnlyMentor
public class UpdateMentorInfoApiController {
    private final UpdateMentorInfoUseCase updateMentorInfoUseCase;

    @Operation(summary = "멘토 기본정보 수정 Endpoint")
    @PatchMapping("/basic-info")
    public ResponseEntity<Void> updateBasicInfo(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateMentorBasicInfoRequest request
    ) {
        updateMentorInfoUseCase.updateBasicInfo(new UpdateMentorBasicInfoCommand(
                authenticated.id(),
                request.name(),
                request.profileUploadUrl(),
                request.languages(),
                request.school(),
                request.major(),
                request.grade(),
                request.meetingUrl(),
                request.introduction()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘토 비밀번호 수정 Endpoint")
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateMentorPasswordRequest request
    ) {
        updateMentorInfoUseCase.updatePassword(new UpdateMentorPasswordCommand(
                authenticated.id(),
                request.currentPassword(),
                request.updatePassword()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멘토 스케줄 수정 Endpoint")
    @PatchMapping("/schedules")
    public ResponseEntity<Void> updateSchedule(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateMentorScheduleRequest request
    ) {
        updateMentorInfoUseCase.updateSchedule(new UpdateMentorScheduleCommand(
                authenticated.id(),
                request.toSchedules()
        ));
        return ResponseEntity.noContent().build();
    }
}
