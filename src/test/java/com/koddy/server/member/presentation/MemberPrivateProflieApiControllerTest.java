package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.GetMemberPrivateProfileUseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeProfile;
import com.koddy.server.member.application.usecase.query.response.MentorProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberPrivateProflieApiController.class)
@DisplayName("Member -> MemberPrivateProflieApiController 테스트")
class MemberPrivateProflieApiControllerTest extends ControllerTest {
    @MockBean
    private GetMemberPrivateProfileUseCase getMemberPrivateProfileUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("사용자 프로필 조회 API [GET /api/members/me]")
    class GetProfile {
        private static final String BASE_URL = "/api/members/me";

        @Test
        @DisplayName("멘토 프로필을 조회한다")
        void getMentorProfile() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getAuthorities());
            given(getMemberPrivateProfileUseCase.getMentorProfile(any())).willReturn(new MentorProfile(mentor));

            // when
            final RequestBuilder requestBuilder = getWithAccessToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocsWithAccessToken("MemberApi/PrivateProfile/Mix/Mentor", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.mainLanguage", "메인 언어 (1개)"),
                                    body("languages.subLanguages[]", "서브 언어 (0..N개)"),
                                    body("university", "대학 정보"),
                                    body("university.school", "학교"),
                                    body("university.major", "전공"),
                                    body("university.enteredIn", "학번"),
                                    body("schedules", "스케줄"),
                                    body("schedules[].day", "날짜", "월 화 수 목 금 토 일"),
                                    body("schedules[].start.hour", "시작 시간 (Hour)"),
                                    body("schedules[].start.minute", "시작 시간 (Minute)"),
                                    body("schedules[].end.hour", "종료 시간 (Hour)"),
                                    body("schedules[].end.minute", "종료 시간 (Minute)"),
                                    body("role", "역할 (멘토/멘티)")
                            )
                    )));
        }

        @Test
        @DisplayName("멘티 프로필을 조회한다")
        void getMenteeProfile() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getAuthorities());
            given(getMemberPrivateProfileUseCase.getMenteeProfile(any())).willReturn(new MenteeProfile(mentee));

            // when
            final RequestBuilder requestBuilder = getWithAccessToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocsWithAccessToken("MemberApi/PrivateProfile/Mix/Mentee", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.mainLanguage", "메인 언어 (1개)"),
                                    body("languages.subLanguages[]", "서브 언어 (0..N개)"),
                                    body("interest", "관심있는 대학 정보"),
                                    body("interest.school", "학교"),
                                    body("interest.major", "전공"),
                                    body("role", "역할 (멘토/멘티)")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("멘토 프로필 조회 API [GET /api/mentors/me]")
    class GetMentorProfile {
        private static final String BASE_URL = "/api/mentors/me";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getAuthorities());

            // when
            final RequestBuilder requestBuilder = getWithAccessToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/PrivateProfile/Mentor/Failure"));
        }

        @Test
        @DisplayName("멘토 마이페이지 프로필 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getAuthorities());
            given(getMemberPrivateProfileUseCase.getMentorProfile(any())).willReturn(new MentorProfile(mentor));

            // when
            final RequestBuilder requestBuilder = getWithAccessToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocsWithAccessToken("MemberApi/PrivateProfile/Mentor/Success", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.mainLanguage", "메인 언어 (1개)"),
                                    body("languages.subLanguages[]", "서브 언어 (0..N개)"),
                                    body("university", "대학 정보"),
                                    body("university.school", "학교"),
                                    body("university.major", "전공"),
                                    body("university.enteredIn", "학번"),
                                    body("schedules", "스케줄"),
                                    body("schedules[].day", "날짜", "월 화 수 목 금 토 일"),
                                    body("schedules[].start.hour", "시작 시간 (Hour)"),
                                    body("schedules[].start.minute", "시작 시간 (Minute)"),
                                    body("schedules[].end.hour", "종료 시간 (Hour)"),
                                    body("schedules[].end.minute", "종료 시간 (Minute)"),
                                    body("role", "역할 (멘토/멘티)")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("멘티 프로필 조회 API [GET /api/mentees/me]")
    class GetMenteeProfile {
        private static final String BASE_URL = "/api/mentees/me";

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getAuthorities());

            // when
            final RequestBuilder requestBuilder = getWithAccessToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/PrivateProfile/Mentee/Failure"));
        }

        @Test
        @DisplayName("멘티 마이페이지 프로필 정보를 조회한다")
        void success() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getAuthorities());
            given(getMemberPrivateProfileUseCase.getMenteeProfile(any())).willReturn(new MenteeProfile(mentee));

            // when
            final RequestBuilder requestBuilder = getWithAccessToken(BASE_URL);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocsWithAccessToken("MemberApi/PrivateProfile/Mentee/Success", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.mainLanguage", "메인 언어 (1개)"),
                                    body("languages.subLanguages[]", "서브 언어 (0..N개)"),
                                    body("interest", "관심있는 대학 정보"),
                                    body("interest.school", "학교"),
                                    body("interest.major", "전공"),
                                    body("role", "역할 (멘토/멘티)")
                            )
                    )));
        }
    }
}
