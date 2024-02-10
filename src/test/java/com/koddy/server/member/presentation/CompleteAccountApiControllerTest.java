package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.common.fixture.TimelineFixture;
import com.koddy.server.member.application.usecase.CompleteProfileUseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.CompleteMenteeProfileRequest;
import com.koddy.server.member.presentation.dto.request.CompleteMentorProfileRequest;
import com.koddy.server.member.presentation.dto.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.dto.request.MentoringPeriodRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> CompleteAccountApiController 테스트")
class CompleteAccountApiControllerTest extends ControllerTest {
    @Autowired
    private CompleteProfileUseCase completeProfileUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);

    @Nested
    @DisplayName("멘토 프로필 완성 API [POST /api/mentors/me/complete]")
    class CompleteMentor {
        private static final String BASE_URL = "/api/mentors/me/complete";
        private final CompleteMentorProfileRequest request = new CompleteMentorProfileRequest(
                MENTOR_1.getIntroduction(),
                MENTOR_1.getProfileImageUrl(),
                new MentoringPeriodRequest(
                        MENTOR_1.getMentoringPeriod().getStartDate(),
                        MENTOR_1.getMentoringPeriod().getEndDate()
                ),
                TimelineFixture.월_수_금()
                        .stream()
                        .map(it -> new MentorScheduleRequest(
                                it.getDayOfWeek().getKor(),
                                new MentorScheduleRequest.Start(
                                        it.getStartTime().getHour(),
                                        it.getStartTime().getMinute()
                                ),
                                new MentorScheduleRequest.End(
                                        it.getEndTime().getHour(),
                                        it.getEndTime().getMinute()
                                )
                        ))
                        .toList()
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Complete/Mentor/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("introduction", "자기소개", false),
                                    body("profileImageUrl", "프로필 이미지", false),
                                    body("period", "멘토링 시간 관련 설정", false),
                                    body("period.startDate", "멘토링 시작 날짜", "[KST] yyyy-MM-dd", false),
                                    body("period.endDate", "멘토링 종료 날짜", "[KST] yyyy-MM-dd", false),
                                    body("schedules", "멘토링 스케줄", false),
                                    body("schedules[].dayOfWeek", "날짜", "월 화 수 목 금 토 일", false),
                                    body("schedules[].start.hour", "시작 시간 (Hour)", "0 ~ 23", false),
                                    body("schedules[].start.minute", "시작 시간 (Minute)", "0 ~ 59", false),
                                    body("schedules[].end.hour", "종료 시간 (Hour)", "0 ~ 23", false),
                                    body("schedules[].end.minute", "종료 시간 (Minute)", "0 ~ 59", false)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토 프로필을 완성한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(completeProfileUseCase)
                    .completeMentor(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Complete/Mentor/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("introduction", "자기소개", false),
                                    body("profileImageUrl", "프로필 이미지", false),
                                    body("period", "멘토링 시간 관련 설정", false),
                                    body("period.startDate", "멘토링 시작 날짜", "[KST] yyyy-MM-dd", false),
                                    body("period.endDate", "멘토링 종료 날짜", "[KST] yyyy-MM-dd", false),
                                    body("schedules", "멘토링 스케줄", false),
                                    body("schedules[].dayOfWeek", "날짜", "월 화 수 목 금 토 일", false),
                                    body("schedules[].start.hour", "시작 시간 (Hour)", "0 ~ 23", false),
                                    body("schedules[].start.minute", "시작 시간 (Minute)", "0 ~ 59", false),
                                    body("schedules[].end.hour", "종료 시간 (Hour)", "0 ~ 23", false),
                                    body("schedules[].end.minute", "종료 시간 (Minute)", "0 ~ 59", false)
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티 프로필 완성 API [POST /api/mentees/me/complete]")
    class CompleteMentee {
        private static final String BASE_URL = "/api/mentees/me/complete";
        private final CompleteMenteeProfileRequest request = new CompleteMenteeProfileRequest(
                MENTEE_1.getIntroduction(),
                MENTEE_1.getProfileImageUrl()
        );

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor);

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Complete/Mentee/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("introduction", "자기소개", false),
                                    body("profileImageUrl", "프로필 이미지", false)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티 프로필을 완성한다")
        void success() {
            // given
            applyToken(true, mentee);
            doNothing()
                    .when(completeProfileUseCase)
                    .completeMentee(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Complete/Mentee/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("introduction", "자기소개", false),
                                    body("profileImageUrl", "프로필 이미지", false)
                            )
                    ))
            );
        }
    }
}
