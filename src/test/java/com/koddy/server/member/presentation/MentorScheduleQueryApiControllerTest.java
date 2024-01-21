package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.GetReservedScheduleUseCase;
import com.koddy.server.member.application.usecase.query.response.ReservedSchedule;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
                    MentoringPeriod.TimeUnit.HALF_HOUR.getValue(),
                    List.of(
                            new ReservedSchedule.Period(
                                    LocalDateTime.of(2024, 2, 3, 18, 30),
                                    LocalDateTime.of(2024, 2, 3, 19, 0)
                            ),
                            new ReservedSchedule.Period(
                                    LocalDateTime.of(2024, 2, 10, 18, 30),
                                    LocalDateTime.of(2024, 2, 10, 19, 0)
                            ),
                            new ReservedSchedule.Period(
                                    LocalDateTime.of(2024, 2, 12, 18, 30),
                                    LocalDateTime.of(2024, 2, 12, 19, 0)
                            ),
                            new ReservedSchedule.Period(
                                    LocalDateTime.of(2024, 2, 21, 18, 30),
                                    LocalDateTime.of(2024, 2, 21, 19, 0)
                            ),
                            new ReservedSchedule.Period(
                                    LocalDateTime.of(2024, 2, 24, 18, 30),
                                    LocalDateTime.of(2024, 2, 24, 19, 0)
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
                                    body("timeUnit", "멘토링 시간 단위", "minute 단위 -> 30 / 60"),
                                    body("periods", "예약된 시간", "오름차순으로 제공"),
                                    body("periods[].start", "예약된 시작 시간", "KST"),
                                    body("periods[].end", "예약된 종료 시간", "KST")
                            )
                    ))
            );
        }
    }
}
