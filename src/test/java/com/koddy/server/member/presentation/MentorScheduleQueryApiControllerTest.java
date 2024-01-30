package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.GetReservedScheduleUseCase;
import com.koddy.server.member.application.usecase.query.response.ReservedSchedule;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import com.koddy.server.member.domain.model.response.MentoringPeriodResponse;
import com.koddy.server.member.domain.model.response.ScheduleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.query;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> MentorScheduleQueryApiController 테스트")
class MentorScheduleQueryApiControllerTest extends ControllerTest {
    @Autowired
    private GetReservedScheduleUseCase getReservedScheduleUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("특정 Year-Month에 대해서 멘토의 예약된 스케줄 조회 API [GET /api/mentors/{mentorId}/reserved-schedule]")
    class GetReservedSchedule {
        private static final String BASE_URL = "/api/mentors/{mentorId}/reserved-schedule";

        @Test
        @DisplayName("특정 Year-Month에 대해서 멘토의 예약된 스케줄을 조회한다")
        void success() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            given(getReservedScheduleUseCase.invoke(any())).willReturn(new ReservedSchedule(
                    new MentoringPeriodResponse(
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31)
                    ),
                    List.of(
                            new ScheduleResponse(
                                    "월",
                                    new ScheduleResponse.Start(18, 0),
                                    new ScheduleResponse.End(23, 0)
                            ),
                            new ScheduleResponse(
                                    "수",
                                    new ScheduleResponse.Start(18, 0),
                                    new ScheduleResponse.End(23, 0)
                            )
                    ),
                    MentoringPeriod.TimeUnit.HALF_HOUR.getValue(),
                    List.of(
                            new ReservedSchedule.Reserved(
                                    LocalDateTime.of(2024, 2, 7, 18, 30),
                                    LocalDateTime.of(2024, 2, 7, 19, 0)
                            ),
                            new ReservedSchedule.Reserved(
                                    LocalDateTime.of(2024, 2, 12, 18, 30),
                                    LocalDateTime.of(2024, 2, 12, 19, 0)
                            ),
                            new ReservedSchedule.Reserved(
                                    LocalDateTime.of(2024, 2, 14, 18, 30),
                                    LocalDateTime.of(2024, 2, 14, 19, 0)
                            ),
                            new ReservedSchedule.Reserved(
                                    LocalDateTime.of(2024, 2, 19, 18, 30),
                                    LocalDateTime.of(2024, 2, 19, 19, 0)
                            ),
                            new ReservedSchedule.Reserved(
                                    LocalDateTime.of(2024, 2, 26, 18, 30),
                                    LocalDateTime.of(2024, 2, 26, 19, 0)
                            )
                    )
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(new UrlWithVariables(BASE_URL, mentor.getId()), Map.of(
                            "year", "2024",
                            "month", "2"
                    )),
                    status().isOk(),
                    successDocsWithAccessToken("MemberApi/Mentor/ReservedSchedule", createHttpSpecSnippets(
                            pathParameters(
                                    path("mentorId", "멘토 ID(PK)", true)
                            ),
                            queryParameters(
                                    query("year", "Year 정보", true),
                                    query("month", "Month 정보", true)
                            ),
                            responseFields(
                                    body("period", "멘토링 시간 관련 설정", "Nullable"),
                                    body("period.startDate", "멘토링 시작 날짜", "[KST] yyyy-MM-dd"),
                                    body("period.endDate", "멘토링 종료 날짜", "[KST] yyyy-MM-dd"),
                                    body("schedules", "스케줄", "0..N개"),
                                    body("schedules[].dayOfWeek", "날짜", "월 화 수 목 금 토 일"),
                                    body("schedules[].start.hour", "시작 시간 (Hour)"),
                                    body("schedules[].start.minute", "시작 시간 (Minute)"),
                                    body("schedules[].end.hour", "종료 시간 (Hour)"),
                                    body("schedules[].end.minute", "종료 시간 (Minute)"),
                                    body("timeUnit", "멘토링 시간 단위", "minute 단위 (30, 60, ...)"),
                                    body("reserved", "예약된 시간", "오름차순으로 제공"),
                                    body("reserved[].start", "예약된 시작 시간", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59"),
                                    body("reserved[].end", "예약된 종료 시간", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59")
                            )
                    ))
            );
        }
    }
}
