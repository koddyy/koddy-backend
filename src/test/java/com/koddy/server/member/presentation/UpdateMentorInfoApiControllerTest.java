package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.UpdateMentorInfoUseCase;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorBasicInfoRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorScheduleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalTime;
import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static com.koddy.server.member.domain.model.mentor.Day.MON;
import static com.koddy.server.member.domain.model.mentor.Day.WED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateMentorInfoApiController.class)
@DisplayName("Member -> UpdateMentorInfoApiController 테스트")
class UpdateMentorInfoApiControllerTest extends ControllerTest {
    @MockBean
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
                        .map(Language::getCode)
                        .toList(),
                MENTOR_1.getUniversityProfile().getSchool(),
                MENTOR_1.getUniversityProfile().getMajor(),
                MENTOR_1.getUniversityProfile().getEnteredIn()
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getRoleTypes());
            doThrow(new AuthException(INVALID_PERMISSION))
                    .when(updateMentorInfoUseCase)
                    .updateBasicInfo(any());

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
                                    body("languages", "사용 가능한 언어", "[국가코드 기반] KR EN CH JP VN", true),
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
            mockingToken(true, mentor.getId(), mentor.getRoleTypes());
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
                                    body("languages", "사용 가능한 언어", "[국가코드 기반] KR EN CH JP VN", true),
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
                new MentorScheduleRequest(MON.getKor(), LocalTime.of(9, 0), LocalTime.of(17, 0)),
                new MentorScheduleRequest(WED.getKor(), LocalTime.of(13, 0), LocalTime.of(20, 0))
        ));

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getRoleTypes());
            doThrow(new AuthException(INVALID_PERMISSION))
                    .when(updateMentorInfoUseCase)
                    .updateSchedule(any());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Update/Mentor/Schedule/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("schedules", "멘토링 스케줄", false),
                                    body("schedules[].day", "날짜", "월 화 수 목 금 토 일", false),
                                    body("schedules[].startTime", "시작 시간", false),
                                    body("schedules[].endTime", "종료 시간", false)
                            )
                    )));
        }

        @Test
        @DisplayName("멘토 스케줄을 수정한다")
        void success() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getRoleTypes());
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
                                    body("schedules[].day", "날짜", "월 화 수 목 금 토 일", false),
                                    body("schedules[].startTime", "시작 시간", false),
                                    body("schedules[].endTime", "종료 시간", false)
                            )
                    )));
        }
    }
}
