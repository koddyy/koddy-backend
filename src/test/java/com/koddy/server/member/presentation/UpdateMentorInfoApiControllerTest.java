package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.UpdateMentorInfoUseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.LanguageRequest;
import com.koddy.server.member.presentation.dto.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorBasicInfoRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorScheduleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDate;
import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
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

@DisplayName("Member -> UpdateMentorInfoApiController 테스트")
class UpdateMentorInfoApiControllerTest extends ControllerTest {
    @Autowired
    private UpdateMentorInfoUseCase updateMentorInfoUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토 기본정보 수정 API [PATCH /api/mentors/me/basic-info] - Required AccessToken")
    class UpdateBasicInfo {
        private static final String BASE_URL = "/api/mentors/me/basic-info";
        private final UpdateMentorBasicInfoRequest request = new UpdateMentorBasicInfoRequest(
                MENTOR_1.getName(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getIntroduction(),
                MENTOR_1.getLanguages()
                        .stream()
                        .map(it -> new LanguageRequest(it.getCategory().getCode(), it.getType().getValue()))
                        .toList(),
                MENTOR_1.getUniversityProfile().getSchool(),
                MENTOR_1.getUniversityProfile().getMajor(),
                MENTOR_1.getUniversityProfile().getEnteredIn()
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Update/Mentor/BasicInfo/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("introduction", "멘토 자기소개", false),
                                    body("languages", "사용 가능한 언어", true),
                                    body("languages[].category", "언어 종류", "[국가코드 기반] KR EN CH JP VN", true),
                                    body("languages[].type", "언어 타입", "메인 언어 (1개) / 서브 언어 (0..N개)", true),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("enteredIn", "학번", true)
                            )
                    )));
        }

        @Test
        @DisplayName("멘토 기본정보를 수정한다")
        void success() throws Exception {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doNothing()
                    .when(updateMentorInfoUseCase)
                    .updateBasicInfo(any());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Update/Mentor/BasicInfo/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("introduction", "멘토 자기소개", false),
                                    body("languages", "사용 가능한 언어", true),
                                    body("languages[].category", "언어 종류", "[국가코드 기반] KR EN CH JP VN", true),
                                    body("languages[].type", "언어 타입", "메인 언어 (1개) / 서브 언어 (0..N개)", true),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("enteredIn", "학번", true)
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("멘토 스케줄 수정 API [PATCH /api/mentors/me/schedules] - Required AccessToken")
    class UpdateSchedule {
        private static final String BASE_URL = "/api/mentors/me/schedules";
        private final UpdateMentorScheduleRequest request = new UpdateMentorScheduleRequest(List.of(
                new MentorScheduleRequest(
                        LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 1),
                        MON.getKor(), new MentorScheduleRequest.Start(9, 0), new MentorScheduleRequest.End(17, 0)
                ),
                new MentorScheduleRequest(
                        LocalDate.of(2024, 2, 3), LocalDate.of(2024, 5, 2),
                        WED.getKor(), new MentorScheduleRequest.Start(13, 0), new MentorScheduleRequest.End(20, 0)
                )
        ));

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Update/Mentor/Schedule/Failure", createHttpSpecSnippets(
                            requestFields(
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

        @Test
        @DisplayName("멘토 스케줄을 수정한다")
        void success() throws Exception {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doNothing()
                    .when(updateMentorInfoUseCase)
                    .updateSchedule(any());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Update/Mentor/Schedule/Success", createHttpSpecSnippets(
                            requestFields(
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
}
