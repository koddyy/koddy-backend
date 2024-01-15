package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.GetMemberPrivateProfileUseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeProfile;
import com.koddy.server.member.application.usecase.query.response.MentorProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> MemberPrivateProflieApiController 테스트")
class MemberPrivateProflieApiControllerTest extends ControllerTest {
    @Autowired
    private GetMemberPrivateProfileUseCase getMemberPrivateProfileUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("사용자 프로필 조회 API [GET /api/members/me]")
    class GetProfile {
        private static final String BASE_URL = "/api/members/me";

        @Test
        @DisplayName("멘토 프로필을 조회한다")
        void getMentorProfile() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            given(getMemberPrivateProfileUseCase.getMentorProfile(mentor.getId())).willReturn(MentorProfile.of(mentor));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isOk(),
                    successDocsWithAccessToken("MemberApi/PrivateProfile/Mix/Mentor", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.main", "메인 언어 (1개)"),
                                    body("languages.sub[]", "서브 언어 (0..N개)"),
                                    body("school", "학교"),
                                    body("major", "전공"),
                                    body("enteredIn", "학번"),
                                    body("authenticated", "대학 인증 여부"),
                                    body("period", "멘토링 시간 관련 설정"),
                                    body("period.startDate", "멘토링 시작 날짜"),
                                    body("period.endDate", "멘토링 종료 날짜"),
                                    body("schedules", "스케줄"),
                                    body("schedules[].dayOfWeek", "날짜", "월 화 수 목 금 토 일"),
                                    body("schedules[].start.hour", "시작 시간 (Hour)", "KST"),
                                    body("schedules[].start.minute", "시작 시간 (Minute)", "KST"),
                                    body("schedules[].end.hour", "종료 시간 (Hour)", "KST"),
                                    body("schedules[].end.minute", "종료 시간 (Minute)", "KST"),
                                    body("role", "역할 (멘토/멘티)"),
                                    body("profileComplete", "프로필 완성 여부 (자기소개, 멘토링 기간, 스케줄)")
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티 프로필을 조회한다")
        void getMenteeProfile() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            given(getMemberPrivateProfileUseCase.getMenteeProfile(mentee.getId())).willReturn(MenteeProfile.of(mentee));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isOk(),
                    successDocsWithAccessToken("MemberApi/PrivateProfile/Mix/Mentee", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.main", "메인 언어 (1개)"),
                                    body("languages.sub[]", "서브 언어 (0..N개)"),
                                    body("interestSchool", "관심있는 학교"),
                                    body("interestMajor", "관심있는 전공"),
                                    body("role", "역할 (멘토/멘티)"),
                                    body("profileComplete", "프로필 완성 여부 (자기소개)")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘토 프로필 조회 API [GET /api/mentors/me]")
    class GetMentorProfile {
        private static final String BASE_URL = "/api/mentors/me";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/PrivateProfile/Mentor/Failure")
            );
        }

        @Test
        @DisplayName("멘토 마이페이지 프로필 정보를 조회한다")
        void success() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            given(getMemberPrivateProfileUseCase.getMentorProfile(mentor.getId())).willReturn(MentorProfile.of(mentor));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isOk(),
                    successDocsWithAccessToken("MemberApi/PrivateProfile/Mentor/Success", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.main", "메인 언어 (1개)"),
                                    body("languages.sub[]", "서브 언어 (0..N개)"),
                                    body("school", "학교"),
                                    body("major", "전공"),
                                    body("enteredIn", "학번"),
                                    body("authenticated", "대학 인증 여부"),
                                    body("period", "멘토링 시간 관련 설정"),
                                    body("period.startDate", "멘토링 시작 날짜"),
                                    body("period.endDate", "멘토링 종료 날짜"),
                                    body("schedules", "스케줄"),
                                    body("schedules[].dayOfWeek", "날짜", "월 화 수 목 금 토 일"),
                                    body("schedules[].start.hour", "시작 시간 (Hour)", "KST"),
                                    body("schedules[].start.minute", "시작 시간 (Minute)", "KST"),
                                    body("schedules[].end.hour", "종료 시간 (Hour)", "KST"),
                                    body("schedules[].end.minute", "종료 시간 (Minute)", "KST"),
                                    body("role", "역할 (멘토/멘티)"),
                                    body("profileComplete", "프로필 완성 여부 (자기소개, 멘토링 기간, 스케줄)")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티 프로필 조회 API [GET /api/mentees/me]")
    class GetMenteeProfile {
        private static final String BASE_URL = "/api/mentees/me";

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/PrivateProfile/Mentee/Failure")
            );
        }

        @Test
        @DisplayName("멘티 마이페이지 프로필 정보를 조회한다")
        void success() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            given(getMemberPrivateProfileUseCase.getMenteeProfile(mentee.getId())).willReturn(MenteeProfile.of(mentee));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isOk(),
                    successDocsWithAccessToken("MemberApi/PrivateProfile/Mentee/Success", createHttpSpecSnippets(
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("email", "이메일"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.main", "메인 언어 (1개)"),
                                    body("languages.sub[]", "서브 언어 (0..N개)"),
                                    body("interestSchool", "관심있는 학교"),
                                    body("interestMajor", "관심있는 전공"),
                                    body("role", "역할 (멘토/멘티)"),
                                    body("profileComplete", "프로필 완성 여부 (자기소개)")
                            )
                    ))
            );
        }
    }
}
