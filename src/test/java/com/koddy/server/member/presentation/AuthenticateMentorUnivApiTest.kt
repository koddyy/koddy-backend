package com.koddy.server.member.presentation

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.STRING
import com.koddy.server.global.exception.GlobalExceptionCode
import com.koddy.server.member.application.usecase.AuthenticateMentorUnivUseCase
import com.koddy.server.member.presentation.request.AuthenticationConfirmWithMailRequest
import com.koddy.server.member.presentation.request.AuthenticationWithMailRequest
import com.koddy.server.member.presentation.request.AuthenticationWithProofDataRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(AuthenticateMentorUnivApi::class)
@DisplayName("Member -> AuthenticateMentorUnivApi 테스트")
internal class AuthenticateMentorUnivApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var authenticateMentorUnivUseCase: AuthenticateMentorUnivUseCase

    @Nested
    @DisplayName("메일 인증 시도 API [POST /api/mentors/me/univ/mail]")
    internal inner class AuthWithMail {
        private val baseUrl = "/api/mentors/me/univ/mail"
        private val request = AuthenticationWithMailRequest(schoolMail = "sjiwon@kyonggi.ac.kr")

        private val requestFields: Array<DocumentField> = arrayOf(
            "schoolMail" type STRING means "인증을 진행할 학교 메일",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            postRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Failure/Case1") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `파악할 수 없는 대학교 도메인은 임시적으로 인증이 불가능하다`() {
            val exceptionCode = GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN

            postRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(AuthenticationWithMailRequest(schoolMail = "sjiwon@kyonggi.edu"))
            }.andExpect {
                status { isBadRequest() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Failure/Case2") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `학교 인증을 메일로 시도한다`() {
            justRun { authenticateMentorUnivUseCase.attemptWithMail(any()) }

            postRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Success") {
                    requestFields(*requestFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("메일 인증 확인 API [POST /api/mentors/me/univ/mail/confirm]")
    internal inner class ConfirmMailAuthCode {
        private val baseUrl = "/api/mentors/me/univ/mail/confirm"
        private val request = AuthenticationConfirmWithMailRequest(
            schoolMail = "sjiwon@kyonggi.ac.kr",
            authCode = "123456",
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "schoolMail" type STRING means "인증을 진행할 학교 메일",
            "authCode" type STRING means "메일로 받은 인증번호",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            postRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Failure/Case1") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `인증번호가 일치하지 않으면 실패한다`() {
            val exceptionCode = AuthExceptionCode.INVALID_AUTH_CODE
            every { authenticateMentorUnivUseCase.confirmMailAuthCode(any()) } throws AuthException(exceptionCode)

            postRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isConflict() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/Failure/Case2") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `학교 메일로 받은 인증번호를 확인한다`() {
            justRun { authenticateMentorUnivUseCase.confirmMailAuthCode(any()) }

            postRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Mentor/UnivAuth/Mail/Confirm/mentor") {
                    requestFields(*requestFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("증명자료 인증 시도 API [POST /api/mentors/me/univ/proof-data]")
    internal inner class AuthWithProofData {
        private val baseUrl = "/api/mentors/me/univ/proof-data"
        private val request = AuthenticationWithProofDataRequest(proofDataUploadUrl = "https://proof-data-upload-url")

        private val requestFields: Array<DocumentField> = arrayOf(
            "proofDataUploadUrl" type STRING means "Presigned로 업로드한 증명 자료 URL" constraint "PDF 파일 + 하나만 업로드",
        )

        @Test
        fun `멘토가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            postRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Mentor/UnivAuth/ProofData/Failure") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `학교 인증을 증명 자료로 시도한다`() {
            justRun { authenticateMentorUnivUseCase.attemptWithProofData(any()) }

            postRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Mentor/UnivAuth/ProofData/Success") {
                    requestFields(*requestFields)
                }
            }
        }
    }
}
