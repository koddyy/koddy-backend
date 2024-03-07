package com.koddy.server.member.presentation

import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.presentation.response.LoginResponse
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.utils.TokenDummy
import com.koddy.server.member.application.usecase.DeleteMemberUseCase
import com.koddy.server.member.application.usecase.SignUpUseCase
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.presentation.request.SignUpMenteeRequest
import com.koddy.server.member.presentation.request.SignUpMentorRequest
import com.koddy.server.member.presentation.request.model.LanguageRequestModel
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders

@WebMvcTest(ManageAccountApi::class)
@DisplayName("Member -> ManageAccountApi 테스트")
internal class ManageAccountApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var signUpUseCase: SignUpUseCase

    @MockkBean
    private lateinit var deleteMemberUseCase: DeleteMemberUseCase

    @Nested
    @DisplayName("멘토 회원가입 + 로그인 API [POST /api/mentors]")
    internal inner class SignUpMentor {
        private val baseUrl = "/api/mentors"
        private val request = SignUpMentorRequest(
            provider = mentor.platform.provider.value,
            socialId = mentor.platform.socialId!!,
            email = mentor.platform.email!!.value,
            name = mentor.name,
            languages = LanguageRequestModel(
                main = Language.Category.KR.code,
                sub = listOf(
                    Language.Category.EN.code,
                    Language.Category.JP.code,
                    Language.Category.CN.code,
                ),
            ),
            school = mentor.universityProfile.school,
            major = mentor.universityProfile.major,
            enteredIn = mentor.universityProfile.enteredIn,
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "provider" type STRING means "소셜 플랫폼" constraint "google kakao",
            "socialId" type STRING means "소셜 플랫폼 고유 ID" constraint "서버에서 응답한 SocialID 그대로 포함",
            "email" type STRING means "이메일",
            "name" type STRING means "이름",
            "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "languages.main" type STRING means "메인 언어" constraint "1개",
            "languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개" isOptional true,
            "school" type STRING means "학교",
            "major" type STRING means "전공",
            "enteredIn" type NUMBER means "학번",
        )
        private val responseHeaders: Array<DocumentField> = arrayOf(
            AuthToken.ACCESS_TOKEN_HEADER type STRING means "Access Token",
            HttpHeaders.SET_COOKIE type STRING means "Set Refresh Token",
        )
        private val responseCookies: Array<DocumentField> = arrayOf(
            AuthToken.REFRESH_TOKEN_HEADER type STRING means "Refresh Token",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "id" type NUMBER means "사용자 ID(PK)",
            "name" type STRING means "사용자 이름",
        )

        @Test
        fun `멘토 회원가입 + 로그인을 진행한다`() {
            val response = AuthMember(
                id = mentor.id,
                name = mentor.name,
                token = TokenDummy.basicAuthToken(),
            )
            every { signUpUseCase.signUpMentor(any()) } returns response

            postRequest(baseUrl) {
                bodyContent(request)
            }.andExpect {
                status { isOk() }
                content { success(LoginResponse(id = response.id, name = response.name)) }
            }.andDo {
                makeSuccessDocs("MemberApi/SignUp/Mentor") {
                    requestFields(*requestFields)
                    responseHeaders(*responseHeaders)
                    responseCookies(*responseCookies)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("멘티 회원가입 + 로그인 API [POST /api/mentees]")
    internal inner class SignUpMentee {
        private val baseUrl = "/api/mentees"
        private val request = SignUpMenteeRequest(
            provider = mentee.platform.provider.value,
            socialId = mentee.platform.socialId!!,
            email = mentee.platform.email!!.value,
            name = mentee.name,
            languages = LanguageRequestModel(
                main = Language.Category.KR.code,
                sub = listOf(
                    Language.Category.EN.code,
                    Language.Category.JP.code,
                    Language.Category.CN.code,
                ),
            ),
            nationality = mentee.nationality.code,
            interestSchool = mentee.interest.school,
            interestMajor = mentee.interest.major,
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "provider" type STRING means "소셜 플랫폼" constraint "google kakao",
            "socialId" type STRING means "소셜 플랫폼 고유 ID" constraint "서버에서 응답한 SocialID 그대로 포함",
            "email" type STRING means "이메일",
            "name" type STRING means "이름",
            "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "languages.main" type STRING means "메인 언어" constraint "1개",
            "languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개" isOptional true,
            "nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
            "interestSchool" type STRING means "관심있는 학교",
            "interestMajor" type STRING means "관심있는 전공",
        )
        private val responseHeaders: Array<DocumentField> = arrayOf(
            AuthToken.ACCESS_TOKEN_HEADER type STRING means "Access Token",
            HttpHeaders.SET_COOKIE type STRING means "Set Refresh Token",
        )
        private val responseCookies: Array<DocumentField> = arrayOf(
            AuthToken.REFRESH_TOKEN_HEADER type STRING means "Refresh Token",
        )
        private val responseFields: Array<DocumentField> = arrayOf(
            "id" type NUMBER means "사용자 ID(PK)",
            "name" type STRING means "사용자 이름",
        )

        @Test
        fun `멘티 회원가입 + 로그인을 진행한다`() {
            val response = AuthMember(
                id = mentee.id,
                name = mentee.name,
                token = TokenDummy.basicAuthToken(),
            )
            every { signUpUseCase.signUpMentee(any()) } returns response

            postRequest(baseUrl) {
                bodyContent(request)
            }.andExpect {
                status { isOk() }
                content { success(LoginResponse(id = response.id, name = response.name)) }
            }.andDo {
                makeSuccessDocs("MemberApi/SignUp/Mentee") {
                    requestFields(*requestFields)
                    responseHeaders(*responseHeaders)
                    responseCookies(*responseCookies)
                    responseFields(*responseFields)
                }
            }
        }
    }

    @Nested
    @DisplayName("사용자 탈퇴 API [DELETE /api/members]")
    internal inner class DeleteMember {
        private val baseUrl = "/api/members"

        @Test
        @DisplayName("서비스를 탙퇴한다")
        fun success() {
            justRun { deleteMemberUseCase.invoke(any()) }

            deleteRequest(baseUrl) {
                accessToken(mentor)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Delete") {}
            }
        }
    }
}
