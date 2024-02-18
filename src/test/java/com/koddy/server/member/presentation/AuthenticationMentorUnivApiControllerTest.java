package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.global.exception.GlobalExceptionCode;
import com.koddy.server.member.application.usecase.AuthenticationMentorUnivUseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.request.AuthenticationConfirmWithMailRequest;
import com.koddy.server.member.presentation.request.AuthenticationWithMailRequest;
import com.koddy.server.member.presentation.request.AuthenticationWithProofDataRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_AUTH_CODE;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
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
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    postRequestWithAccessToken(BASE_URL, request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Failure/Case1", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("파악할 수 없는 대학교 도메인은 임시적으로 인증이 불가능하다")
        void throwExceptionByInvalidUnivDomain() {
            // given
            applyToken(true, mentor);

            // when - then
            failedExecute(
                    postRequestWithAccessToken(BASE_URL, new AuthenticationWithMailRequest("sjiwon@kyonggi.edu")),
                    status().isBadRequest(),
                    ExceptionSpec.of(GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN),
                    failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Failure/Case2", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("학교 인증을 메일로 시도한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(authenticationMentorUnivUseCase)
                    .authWithMail(any());

            // when - then
            successfulExecute(
                    postRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true)
                            )
                    ))
            );
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
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    postRequestWithAccessToken(BASE_URL, request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Failure/Case1", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true),
                                    body("authCode", "인증번호", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("인증번호가 일치하지 않으면 실패한다")
        void throwExceptionByInvalidAuthCode() {
            // given
            applyToken(true, mentor);
            doThrow(new AuthException(INVALID_AUTH_CODE))
                    .when(authenticationMentorUnivUseCase)
                    .confirmMailAuthCode(any());

            // when - then
            failedExecute(
                    postRequestWithAccessToken(BASE_URL, request),
                    status().isConflict(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_AUTH_CODE),
                    failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Failure/Case2", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true),
                                    body("authCode", "인증번호", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("학교 메일로 받은 인증번호를 확인한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(authenticationMentorUnivUseCase)
                    .confirmMailAuthCode(any());

            // when - then
            successfulExecute(
                    postRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("schoolMail", "인증을 진행할 학교 메일", true),
                                    body("authCode", "인증번호", true)
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("증명자료 인증 시도 API [POST /api/mentors/me/univ/proof-data]")
    class AuthWithProofData {
        private static final String BASE_URL = "/api/mentors/me/univ/proof-data";
        private final AuthenticationWithProofDataRequest request = new AuthenticationWithProofDataRequest("upload-url");

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    postRequestWithAccessToken(BASE_URL, request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/ProofData/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("proofDataUploadUrl", "Presigned로 업로드한 증명 자료 URL", "PDF 파일 + 하나만 업로드", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("학교 인증을 증명 자료로 시도한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(authenticationMentorUnivUseCase)
                    .authWithProofData(any());

            // when - then
            successfulExecute(
                    postRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Mentor/UnivAuth/ProofData/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("proofDataUploadUrl", "Presigned로 업로드한 증명 자료 URL", "PDF 파일 + 하나만 업로드", true)
                            )
                    ))
            );
        }
    }
}
