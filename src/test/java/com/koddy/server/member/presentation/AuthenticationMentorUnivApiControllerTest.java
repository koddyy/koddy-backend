package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.AuthenticationMentorUnivUseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.AuthenticationConfirmWithMailRequest;
import com.koddy.server.member.presentation.dto.request.AuthenticationWithMailRequest;
import com.koddy.server.member.presentation.dto.request.AuthenticationWithProofDataRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static com.koddy.server.global.exception.GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> AuthenticationMentorUnivApiController 테스트")
class AuthenticationMentorUnivApiControllerTest extends ControllerTest {
    @Autowired
    private AuthenticationMentorUnivUseCase authenticationMentorUnivUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("메일 인증 시도 API [POST /api/mentors/me/univ/mail]")
    class AuthWithMail {
        private static final String BASE_URL = "/api/mentors/me/univ/mail";
        private final AuthenticationWithMailRequest request = new AuthenticationWithMailRequest("sjiwon@kyonggi.ac.kr");

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Failure/Case1", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true)
                            )
                    )));
        }

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidUnivDomain() throws Exception {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, new AuthenticationWithMailRequest("sjiwon@kyonggi.edu"));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(NOT_PROVIDED_UNIV_DOMAIN))
                    .andDo(failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Failure/Case2", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true)
                            )
                    )));
        }

        @Test
        @DisplayName("학교 인증을 메일로 시도한다")
        void success() throws Exception {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doNothing()
                    .when(authenticationMentorUnivUseCase)
                    .authWithMail(any());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true)
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("메일 인증 확인 API [POST /api/mentors/me/univ/mail/confirm]")
    class ConfirmMailAuthCode {
        private static final String BASE_URL = "/api/mentors/me/univ/mail/confirm";
        private final AuthenticationConfirmWithMailRequest request = new AuthenticationConfirmWithMailRequest(
                "sjiwon@kyonggi.ac.kr",
                UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8)
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Failure/Case1", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true),
                                    body("authCode", "인증번호", true)
                            )
                    )));
        }

        @Test
        @DisplayName("인증번호가 일치하지 않으면 실패한다")
        void throwExceptionByInvalidAuthCode() throws Exception {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doThrow(new AuthException(INVALID_AUTH_CODE))
                    .when(authenticationMentorUnivUseCase)
                    .confirmMailAuthCode(any());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_AUTH_CODE))
                    .andDo(failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Failure/Case2", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true),
                                    body("authCode", "인증번호", true)
                            )
                    )));
        }

        @Test
        @DisplayName("학교 메일로 받은 인증번호를 확인한다")
        void success() throws Exception {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doNothing()
                    .when(authenticationMentorUnivUseCase)
                    .confirmMailAuthCode(any());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true),
                                    body("authCode", "인증번호", true)
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("증명자료 인증 시도 API [POST /api/mentors/me/univ/proof-data]")
    class AuthWithProofData {
        private static final String BASE_URL = "/api/mentors/me/univ/proof-data";
        private final AuthenticationWithProofDataRequest request = new AuthenticationWithProofDataRequest("upload-url");

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/ProofData/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("proofDataUploadUrl", "Presigned로 업로드한 증명 자료 URL", "하나만 업로드", true)
                            )
                    )));
        }

        @Test
        @DisplayName("학교 인증을 증명 자료로 시도한다")
        void success() throws Exception {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doNothing()
                    .when(authenticationMentorUnivUseCase)
                    .authWithProofData(any());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Mentor/UnivAuth/ProofData/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("proofDataUploadUrl", "Presigned로 업로드한 증명 자료 URL", "PDF 파일 + 하나만 업로드", true)
                            )
                    )));
        }
    }
}
