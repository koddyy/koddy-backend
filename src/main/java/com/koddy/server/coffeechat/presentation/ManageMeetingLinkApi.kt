package com.koddy.server.coffeechat.presentation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.ManageMeetingLinkUseCase
import com.koddy.server.coffeechat.application.usecase.command.DeleteMeetingLinkCommand
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse
import com.koddy.server.coffeechat.presentation.request.CreateMeetingLinkRequest
import com.koddy.server.coffeechat.presentation.response.CreateMeetingLinkResponse
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.aop.AccessControl
import com.koddy.server.member.domain.model.Role
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "4-1. 커피챗 링크 생성/삭제 API")
@RestController
@RequestMapping("/api/oauth/{provider}/meetings")
class ManageMeetingLinkApi(
    private val manageMeetingLinkUseCase: ManageMeetingLinkUseCase,
) {
    @Operation(summary = "커피챗 링크 생성 Endpoint")
    @PostMapping
    @AccessControl(role = Role.MENTOR)
    fun create(
        @Auth authenticated: Authenticated,
        @PathVariable provider: String,
        @RequestBody @Valid request: CreateMeetingLinkRequest,
    ): ResponseEntity<CreateMeetingLinkResponse> {
        val result: MeetingLinkResponse = manageMeetingLinkUseCase.create(
            request.toCommand(
                authenticated.id,
                provider,
            ),
        )
        return ResponseEntity.ok(
            CreateMeetingLinkResponse(
                id = result.id(),
                hostEmail = result.hostEmail(),
                topic = result.topic(),
                joinUrl = result.joinUrl(),
                duration = result.duration(),
            ),
        )
    }

    @Operation(summary = "커피챗 링크 삭제 Endpoint")
    @DeleteMapping("/{meetingId}")
    @AccessControl(role = Role.MENTOR)
    fun delete(
        @Auth authenticated: Authenticated,
        @PathVariable provider: String,
        @PathVariable meetingId: String,
    ): ResponseEntity<Void> {
        manageMeetingLinkUseCase.delete(
            DeleteMeetingLinkCommand(
                MeetingLinkProvider.from(provider),
                meetingId,
            ),
        )
        return ResponseEntity.noContent().build()
    }
}
