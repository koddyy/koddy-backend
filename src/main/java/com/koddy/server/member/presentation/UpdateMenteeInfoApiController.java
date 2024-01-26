package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.aop.AccessControl;
import com.koddy.server.member.application.usecase.UpdateMenteeInfoUseCase;
import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand;
import com.koddy.server.member.presentation.dto.request.UpdateMenteeBasicInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.koddy.server.member.domain.model.Role.MENTEE;

@Tag(name = "2-4. 멘티 정보 수정 API")
@RestController
@RequestMapping("/api/mentees/me")
@RequiredArgsConstructor
public class UpdateMenteeInfoApiController {
    private final UpdateMenteeInfoUseCase updateMenteeInfoUseCase;

    @Operation(summary = "멘티 기본정보 수정 Endpoint")
    @PatchMapping("/basic-info")
    @AccessControl(role = MENTEE)
    public ResponseEntity<Void> updateBasicInfo(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateMenteeBasicInfoRequest request
    ) {
        updateMenteeInfoUseCase.updateBasicInfo(new UpdateMenteeBasicInfoCommand(
                authenticated.id(),
                request.name(),
                request.toNationality(),
                request.profileImageUrl(),
                request.introduction(),
                request.toLanguages(),
                request.interestSchool(),
                request.interestMajor()
        ));
        return ResponseEntity.noContent().build();
    }
}
