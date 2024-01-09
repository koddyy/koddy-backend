package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.common.fixture.TimelineFixture;
import com.koddy.server.member.application.usecase.CompleteProfileUseCase;
import com.koddy.server.member.presentation.dto.request.CompleteMenteeProfileRequest;
import com.koddy.server.member.presentation.dto.request.CompleteMentorProfileRequest;
import com.koddy.server.member.presentation.dto.request.MentorScheduleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompleteAccountApiController.class)
@DisplayName("Member -> CompleteAccountApiController 테스트")
class CompleteAccountApiControllerTest extends ControllerTest {
    @MockBean
    private CompleteProfileUseCase completeProfileUseCase;

    @Nested
    @DisplayName("멘토 프로필 완성 API [POST /api/mentors/me/complete]")
    class CompleteMentor {
        private static final String BASE_URL = "/api/mentors/me/complete";

        @Test
        @DisplayName("멘토 프로필을 완성한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(completeProfileUseCase)
                    .completeMentor(any());

            final CompleteMentorProfileRequest request = new CompleteMentorProfileRequest(
                    MENTOR_1.getIntroduction(),
                    TimelineFixture.월_수_금()
                            .stream()
                            .map(it -> new MentorScheduleRequest(
                                    it.getStartDate(),
                                    it.getEndDate(),
                                    it.getDayOfWeek().getKor(),
                                    new MentorScheduleRequest.Start(
                                            it.getPeriod().getStartTime().getHour(),
                                            it.getPeriod().getStartTime().getMinute()
                                    ),
                                    new MentorScheduleRequest.End(
                                            it.getPeriod().getEndTime().getHour(),
                                            it.getPeriod().getEndTime().getMinute()
                                    )
                            ))
                            .toList()
            );

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Complete/Mentor", createHttpSpecSnippets(
                            requestFields(
                                    body("introduction", "자기소개", false),
                                    body("schedules", "멘토링 스케줄", false),
                                    body("schedules[].startDate", "시작 날짜", "KST", false),
                                    body("schedules[].endDate", "종료 날짜", "KST", false),
                                    body("schedules[].dayOfWeek", "날짜", "월 화 수 목 금 토 일", false),
                                    body("schedules[].startTime.hour", "시작 시간 (Hour)", "KST", false),
                                    body("schedules[].startTime.minute", "시작 시간 (Minute)", "KST", false),
                                    body("schedules[].endTime.hour", "종료 시간 (Hour)", "KST", false),
                                    body("schedules[].endTime.minute", "종료 시간 (Minute)", "KST", false)
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("멘티 프로필 완성 API [POST /api/mentees/me/complete")
    class CompleteMentee {
        private static final String BASE_URL = "/api/mentees/me/complete";

        @Test
        @DisplayName("멘티 프로필을 완성한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(completeProfileUseCase)
                    .completeMentee(any());

            final CompleteMenteeProfileRequest request = new CompleteMenteeProfileRequest(MENTEE_1.getIntroduction());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Complete/Mentee", createHttpSpecSnippets(
                            requestFields(
                                    body("introduction", "자기소개", false)
                            )
                    )));
        }
    }
}
