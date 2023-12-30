package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.CompleteInformationUseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.CompleteMenteeRequest;
import com.koddy.server.member.presentation.dto.request.CompleteMentorRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
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

@WebMvcTest(CompleteInformationApiController.class)
@DisplayName("Member -> CompleteInformationApiController 테스트")
class CompleteInformationApiControllerTest extends ControllerTest {
    @MockBean
    private CompleteInformationUseCase completeInformationUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토 부가정보 기입 API [POST /api/members/mentor] - Required AccessToken")
    class CompleteMentor {
        private static final String BASE_URL = "/api/members/mentor";
        private final CompleteMentorRequest request = new CompleteMentorRequest(
                MENTOR_1.getName(),
                MENTOR_1.getNationality(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile().getSchool(),
                MENTOR_1.getUniversityProfile().getMajor(),
                MENTOR_1.getUniversityProfile().getGrade(),
                MENTOR_1.getMeetingUrl(),
                MENTOR_1.getIntroduction(),
                MENTOR_1.getSchedules()
                        .stream()
                        .map(it -> new CompleteMentorRequest.ScheduleRequest(it.getDay(), it.getPeriod().getStartTime(), it.getPeriod().getEndTime()))
                        .toList()
        );

        @Test
        @DisplayName("멘토가 아니면 멘토 부가정보를 기입할 수 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getRoleTypes());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Complete/Mentor/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KOREA\nUSA\nJAPAN\nCHINA\nVIETNAM\nOTHERS", true),
                                    body("profileUploadUrl", "프로필 이미지 URL", "Presigned Url로 업로드한 프로필 이미지 URL\n->기본 이미지 설정이면 null)", false),
                                    body("languages", "사용 가능한 언어", "KOREAN\nENGLISH\nCHINESE\nJAPANESE\nVIETNAMESE", true),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("grade", "학년", true),
                                    body("meetingUrl", "커피챗 링크", true),
                                    body("introduction", "멘토 자기소개", true),
                                    body("schedules", "멘토링 스케줄", true),
                                    body("schedules[].day", "날짜", "MONDAY\nTUESDAY\nWEDNESDAY\nTHURSDAY\nFRIDAY\nSATURDAY\nSUNDAY", true),
                                    body("schedules[].startTime", "시작 시간", true),
                                    body("schedules[].endTime", "종료 시간", true)
                            )
                    )));
        }

        @Test
        @DisplayName("멘토 부가정보를 기입한다")
        void success() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getRoleTypes());
            doNothing()
                    .when(completeInformationUseCase)
                    .completeMentor(any());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Complete/Mentor/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KOREA\nUSA\nJAPAN\nCHINA\nVIETNAM\nOTHERS", true),
                                    body("profileUploadUrl", "프로필 이미지 URL", "Presigned Url로 업로드한 프로필 이미지 URL\n->기본 이미지 설정이면 null)", false),
                                    body("languages", "사용 가능한 언어", "KOREAN\nENGLISH\nCHINESE\nJAPANESE\nVIETNAMESE", true),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("grade", "학년", true),
                                    body("meetingUrl", "커피챗 링크", true),
                                    body("introduction", "멘토 자기소개", true),
                                    body("schedules", "멘토링 스케줄", true),
                                    body("schedules", "멘토링 스케줄", true),
                                    body("schedules[].day", "날짜", "MONDAY\nTUESDAY\nWEDNESDAY\nTHURSDAY\nFRIDAY\nSATURDAY\nSUNDAY", true),
                                    body("schedules[].startTime", "시작 시간", true),
                                    body("schedules[].endTime", "종료 시간", true)
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("멘티 부가정보 기입 API [POST /api/members/mentee] - Required AccessToken")
    class CompleteMentee {
        private static final String BASE_URL = "/api/members/mentee";
        private final CompleteMenteeRequest request = new CompleteMenteeRequest(
                MENTEE_1.getName(),
                MENTEE_1.getNationality(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest().getSchool(),
                MENTEE_1.getInterest().getMajor()
        );

        @Test
        @DisplayName("멘티가 아니면 멘티 부가정보를 기입할 수 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getRoleTypes());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Complete/Mentee/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KOREA\nUSA\nJAPAN\nCHINA\nVIETNAM\nOTHERS", true),
                                    body("profileUploadUrl", "프로필 이미지 URL", "Presigned Url로 업로드한 프로필 이미지 URL\n->기본 이미지 설정이면 null)", false),
                                    body("languages", "사용 가능한 언어", "KOREAN\nENGLISH\nCHINESE\nJAPANESE\nVIETNAMESE", true),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            )
                    )));
        }

        @Test
        @DisplayName("멘티 부가정보를 기입한다")
        void success() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getRoleTypes());
            doNothing()
                    .when(completeInformationUseCase)
                    .completeMentor(any());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Complete/Mentee/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KOREA\nUSA\nJAPAN\nCHINA\nVIETNAM\nOTHERS", true),
                                    body("profileUploadUrl", "프로필 이미지 URL", "Presigned Url로 업로드한 프로필 이미지 URL\n->기본 이미지 설정이면 null)", false),
                                    body("languages", "사용 가능한 언어", "KOREAN\nENGLISH\nCHINESE\nJAPANESE\nVIETNAMESE", true),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            )
                    )));
        }
    }
}
