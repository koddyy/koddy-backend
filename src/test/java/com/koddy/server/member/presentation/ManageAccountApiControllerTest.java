package com.koddy.server.member.presentation;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.DeleteMemberUseCase;
import com.koddy.server.member.application.usecase.SignUpUsecase;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.LanguageRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMentorRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.cookie;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.header;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> ManageAccountApiController 테스트")
class ManageAccountApiControllerTest extends ControllerTest {
    @Autowired
    private SignUpUsecase signUpUsecase;

    @Autowired
    private DeleteMemberUseCase deleteMemberUseCase;

    @Nested
    @DisplayName("멘토 회원가입 + 로그인 API [POST /api/mentors]")
    class SignUpMentor {
        private static final String BASE_URL = "/api/mentors";

        @Test
        @DisplayName("멘토 회원가입 + 로그인을 진행한다")
        void success() {
            // given
            final AuthMember loginResponse = MENTOR_1.toAuthMember();
            given(signUpUsecase.signUpMentor(any())).willReturn(loginResponse);

            final SignUpMentorRequest request = new SignUpMentorRequest(
                    MENTOR_1.getPlatform().getProvider().getValue(),
                    MENTOR_1.getPlatform().getSocialId(),
                    MENTOR_1.getPlatform().getEmail().getValue(),
                    MENTOR_1.getName(),
                    MENTOR_1.getProfileImageUrl(),
                    new LanguageRequest(
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

            // when - then
            successfulExecute(
                    postRequest(BASE_URL, request),
                    status().isOk(),
                    successDocs("MemberApi/SignUp/Mentor", createHttpSpecSnippets(
                            requestFields(
                                    body("provider", "소셜 플랫폼", "google / kakao", true),
                                    body("socialId", "소셜 플랫폼 고유 ID", "서버에서 응답한 값 그대로 포함", true),
                                    body("email", "이메일", true),
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("languages", "사용 가능한 언어", true),
                                    body("languages.main", "메인 언어 (코드 기반)", "1개 이상", true),
                                    body("languages.sub[]", "서브 언어 (코드 기반)", "0..N개", false),
                                    body("school", "학교", true),
                                    body("major", "전공", true),
                                    body("enteredIn", "학번", true)
                            ),
                            responseHeaders(
                                    header(AUTHORIZATION, "Access Token")
                            ),
                            responseCookies(
                                    cookie(COOKIE_REFRESH_TOKEN, "Refresh Token")
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)"),
                                    body("name", "사용자 이름"),
                                    body("profileImageUrl", "사용자 프로필 이미지")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티 회원가입 + 로그인 API [POST /api/mentees]")
    class SignUpMentee {
        private static final String BASE_URL = "/api/mentees";

        @Test
        @DisplayName("멘티 회원가입 + 로그인을 진행한다")
        void success() {
            // given
            final AuthMember loginResponse = MENTEE_1.toAuthMember();
            given(signUpUsecase.signUpMentee(any())).willReturn(loginResponse);

            final SignUpMenteeRequest request = new SignUpMenteeRequest(
                    MENTEE_1.getPlatform().getProvider().getValue(),
                    MENTEE_1.getPlatform().getSocialId(),
                    MENTEE_1.getPlatform().getEmail().getValue(),
                    MENTEE_1.getName(),
                    MENTEE_1.getProfileImageUrl(),
                    MENTEE_1.getNationality().getKor(),
                    new LanguageRequest(Language.Category.KR.getCode(), List.of()),
                    MENTEE_1.getInterest().getSchool(),
                    MENTEE_1.getInterest().getMajor()
            );

            // when - then
            successfulExecute(
                    postRequest(BASE_URL, request),
                    status().isOk(),
                    successDocs("MemberApi/SignUp/Mentee", createHttpSpecSnippets(
                            requestFields(
                                    body("provider", "소셜 플랫폼", "google / kakao", true),
                                    body("socialId", "소셜 플랫폼 고유 ID", "서버에서 응답한 값 그대로 포함", true),
                                    body("email", "이메일", true),
                                    body("name", "이름", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("nationality", "국적", "한국 미국 일본 중국 베트남 Others", true),
                                    body("languages", "사용 가능한 언어", true),
                                    body("languages.main", "메인 언어 (코드 기반)", "1개 이상", true),
                                    body("languages.sub[]", "서브 언어 (코드 기반)", "0..N개", false),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            ),
                            responseHeaders(
                                    header(AUTHORIZATION, "Access Token")
                            ),
                            responseCookies(
                                    cookie(COOKIE_REFRESH_TOKEN, "Refresh Token")
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)"),
                                    body("name", "사용자 이름"),
                                    body("profileImageUrl", "사용자 프로필 이미지")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("사용자 탈퇴 API [DELETE /api/members]")
    class DeleteMember {
        private static final String BASE_URL = "/api/members";
        private final Mentor mentor = MENTOR_1.toDomain().apply(1L);

        @Test
        @DisplayName("서비스를 탙퇴한다")
        void success() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doNothing()
                    .when(deleteMemberUseCase)
                    .invoke(any());

            // when - then
            successfulExecute(
                    deleteRequestWithAccessToken(BASE_URL),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Delete")
            );
        }
    }
}
