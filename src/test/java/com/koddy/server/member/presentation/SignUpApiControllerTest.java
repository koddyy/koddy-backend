package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.SignUpUsecase;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.presentation.dto.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMentorRequest;
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
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignUpApiController.class)
@DisplayName("Member -> SignUpApiController 테스트")
class SignUpApiControllerTest extends ControllerTest {
    @MockBean
    private SignUpUsecase signUpUsecase;

    @Nested
    @DisplayName("멘토 회원가입 API [POST /api/mentors]")
    class SignUpMentor {
        private static final String BASE_URL = "/api/mentors";

        @Test
        @DisplayName("멘토 회원가입을 진행한다")
        void success() throws Exception {
            // given
            given(signUpUsecase.signUpMentor(any())).willReturn(1L);
            final SignUpMentorRequest request = new SignUpMentorRequest(
                    MENTOR_1.getEmail().getValue(),
                    MENTOR_1.getName(),
                    MENTOR_1.getProfileImageUrl(),
                    MENTOR_1.getIntroduction(),
                    MENTOR_1.getLanguages()
                            .stream()
                            .map(Language::getCode)
                            .toList(),
                    MENTOR_1.getUniversityProfile().getSchool(),
                    MENTOR_1.getUniversityProfile().getMajor(),
                    MENTOR_1.getUniversityProfile().getEnteredIn(),
                    MENTOR_1.getSchedules()
                            .stream()
                            .map(it -> new MentorScheduleRequest(it.getDay().getKor(), it.getPeriod().getStartTime(), it.getPeriod().getEndTime()))
                            .toList()
            );

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").exists(),
                            jsonPath("$.id").value(1L)
                    )
                    .andDo(successDocs("MemberApi/SignUp/Mentor", createHttpSpecSnippets(
                            requestFields(
                                    body("email", "이메일", true),
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("introduction", "자기소개", false),
                                    body("languages", "사용 가능한 언어", "[국가코드 기반] KR EN CH JP VN", true),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("enteredIn", "학번", true),
                                    body("schedules", "멘토링 스케줄", false),
                                    body("schedules[].day", "날짜", "월 화 수 목 금 토 일", false),
                                    body("schedules[].startTime", "시작 시간", false),
                                    body("schedules[].endTime", "종료 시간", false)
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("멘티 회원가입 API [POST /api/mentees]")
    class SignUpMentee {
        private static final String BASE_URL = "/api/mentees";

        @Test
        @DisplayName("멘티 회원가입을 진행한다")
        void success() throws Exception {
            // given
            given(signUpUsecase.signUpMentee(any())).willReturn(1L);
            final SignUpMenteeRequest request = new SignUpMenteeRequest(
                    MENTEE_1.getEmail().getValue(),
                    MENTEE_1.getName(),
                    MENTEE_1.getProfileImageUrl(),
                    MENTEE_1.getNationality().getKor(),
                    MENTEE_1.getIntroduction(),
                    MENTEE_1.getLanguages()
                            .stream()
                            .map(Language::getCode)
                            .toList(),
                    MENTEE_1.getInterest().getSchool(),
                    MENTEE_1.getInterest().getMajor()
            );

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").exists(),
                            jsonPath("$.id").value(1L)
                    )
                    .andDo(successDocs("MemberApi/SignUp/Mentee", createHttpSpecSnippets(
                            requestFields(
                                    body("email", "이메일", true),
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("nationality", "국적", "한국 미국 일본 중국 베트남 Others", true),
                                    body("introduction", "자기소개", false),
                                    body("languages", "사용 가능한 언어", "[국가코드 기반] KR EN CH JP VN", true),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)")
                            )
                    )));
        }
    }
}
