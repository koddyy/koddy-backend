package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.UpdateMentorProfileUseCase;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.request.UpdateMentorBasicInfoRequest;
import com.koddy.server.member.presentation.request.UpdateMentorScheduleRequest;
import com.koddy.server.member.presentation.request.model.LanguageRequestModel;
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.MON;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.WED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> UpdateMentorProfileApi 테스트")
class UpdateMentorProfileApiTest extends ControllerTest {
    @Autowired
    private UpdateMentorProfileUseCase updateMentorProfileUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토 기본정보 수정 API [PATCH /api/mentors/me/basic-info]")
    class UpdateBasicInfo {
        private static final String BASE_URL = "/api/mentors/me/basic-info";
        private final UpdateMentorBasicInfoRequest request = new UpdateMentorBasicInfoRequest(
                MENTOR_1.getName(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getIntroduction(),
                new LanguageRequestModel(
                        Language.Category.KR.getCode(),
                        List.of(
                                Language.Category.EN.getCode(),
                                Language.Category.JP.getCode(),
                                Language.Category.CN.getCode()
                        )
                ),
                MENTOR_1.getUniversityProfile().getSchool(),
                MENTOR_1.getUniversityProfile().getMajor(),
                MENTOR_1.getUniversityProfile().getEnteredIn()
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
                    failureDocsWithAccessToken("MemberApi/Update/Mentor/BasicInfo/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", false),
                                    body("introduction", "멘토 자기소개", false),
                                    body("languages", "사용 가능한 언어", "KR EN CN JP VN", true),
                                    body("languages.main", "메인 언어", "1개", true),
                                    body("languages.sub[]", "서브 언어", "0..N개", false),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("enteredIn", "학번", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토 기본정보를 수정한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(updateMentorProfileUseCase)
                    .updateBasicInfo(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Update/Mentor/BasicInfo/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", false),
                                    body("introduction", "멘토 자기소개", false),
                                    body("languages", "사용 가능한 언어", "KR EN CN JP VN", true),
                                    body("languages.main", "메인 언어", "1개", true),
                                    body("languages.sub[]", "서브 언어", "0..N개", false),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("enteredIn", "학번", true)
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘토 스케줄 수정 API [PATCH /api/mentors/me/schedules]")
    class UpdateSchedule {
        private static final String BASE_URL = "/api/mentors/me/schedules";
        private final UpdateMentorScheduleRequest request = new UpdateMentorScheduleRequest(
                new MentoringPeriodRequestModel(
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 5, 1)
                ),
                List.of(
                        new MentorScheduleRequest(MON.getKor(), new MentorScheduleRequest.Start(9, 0), new MentorScheduleRequest.End(17, 0)),
                        new MentorScheduleRequest(WED.getKor(), new MentorScheduleRequest.Start(13, 0), new MentorScheduleRequest.End(20, 0))
                )
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
                    failureDocsWithAccessToken("MemberApi/Update/Mentor/Schedule/Failure", createHttpSpecSnippets(
                            requestFields(
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
        @DisplayName("멘토 스케줄을 수정한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(updateMentorProfileUseCase)
                    .updateSchedule(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Update/Mentor/Schedule/Success", createHttpSpecSnippets(
                            requestFields(
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
}
