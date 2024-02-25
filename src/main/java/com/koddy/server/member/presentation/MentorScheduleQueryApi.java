package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.member.application.usecase.GetReservedScheduleUseCase;
import com.koddy.server.member.application.usecase.query.GetReservedSchedule;
import com.koddy.server.member.application.usecase.query.response.ReservedSchedule;
import com.koddy.server.member.presentation.request.ReservedScheduleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "2-6. 멘토 스케줄 조회 API")
@RestController
@RequestMapping("/api/mentors/{mentorId}/reserved-schedule")
public class MentorScheduleQueryApi {
    private final GetReservedScheduleUseCase getReservedScheduleUseCase;

    public MentorScheduleQueryApi(final GetReservedScheduleUseCase getReservedScheduleUseCase) {
        this.getReservedScheduleUseCase = getReservedScheduleUseCase;
    }

    @Operation(summary = "특정 Year-Month에 대해서 멘토의 예약된 스케줄 조회 Endpoint")
    @GetMapping
    public ResponseEntity<ReservedSchedule> getReservedSchedule(
            @Auth final Authenticated authenticated,
            @PathVariable final Long mentorId,
            @ModelAttribute @Valid final ReservedScheduleRequest request
    ) {
        final ReservedSchedule result = getReservedScheduleUseCase.invoke(new GetReservedSchedule(
                mentorId,
                request.year(),
                request.month()
        ));
        return ResponseEntity.ok(result);
    }
}
