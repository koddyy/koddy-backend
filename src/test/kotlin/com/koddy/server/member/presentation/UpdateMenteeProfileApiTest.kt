package com.koddy.server.member.presentation

import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.member.application.usecase.UpdateMenteeProfileUseCase
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.presentation.request.UpdateMenteeBasicInfoRequest
import com.koddy.server.member.presentation.request.model.LanguageRequestModel
import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(UpdateMenteeProfileApi::class)
@DisplayName("Member -> UpdateMenteeProfileApi 테스트")
internal class UpdateMenteeProfileApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var updateMenteeProfileUseCase: UpdateMenteeProfileUseCase

    @Nested
    @DisplayName("멘티 기본정보 수정 API [PATCH /api/mentees/me/basic-info]")
    internal inner class UpdateBasicInfo {
        private val baseUrl = "/api/mentees/me/basic-info"
        private val request = UpdateMenteeBasicInfoRequest(
            name = mentee.name,
            nationality = mentee.nationality.code,
            profileImageUrl = mentee.profileImageUrl,
            introduction = mentee.introduction,
            languages = LanguageRequestModel(
                main = Language.Category.KR.code,
                sub = listOf(
                    Language.Category.EN.code,
                    Language.Category.JP.code,
                    Language.Category.CN.code,
                ),
            ),
            interestSchool = mentee.interest.school,
            interestMajor = mentee.interest.major,
        )

        private val requestFields: Array<DocumentField> = arrayOf(
            "name" type STRING means "이름",
            "profileImageUrl" type STRING means "프로필 이미지 URL" isOptional true,
            "nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
            "introduction" type STRING means "자기소개" isOptional true,
            "languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "languages.main" type STRING means "메인 언어" constraint "1개",
            "languages.sub[]" type ARRAY means "서브 언어" isOptional true,
            "interestSchool" type STRING means "관심있는 학교",
            "interestMajor" type STRING means "관심있는 전공",
        )

        @Test
        fun `멘티가 아니면 권한이 없다`() {
            val exceptionCode = AuthExceptionCode.INVALID_PERMISSION

            patchRequest(baseUrl) {
                accessToken(mentor)
                bodyContent(request)
            }.andExpect {
                status { isForbidden() }
                content { exception(exceptionCode) }
            }.andDo {
                makeFailureDocsWithAccessToken("MemberApi/Update/Mentee/BasicInfo/Failure") {
                    requestFields(*requestFields)
                }
            }
        }

        @Test
        fun `멘티 기본정보를 수정한다`() {
            justRun { updateMenteeProfileUseCase.updateBasicInfo(any()) }

            patchRequest(baseUrl) {
                accessToken(mentee)
                bodyContent(request)
            }.andExpect {
                status { isNoContent() }
            }.andDo {
                makeSuccessDocsWithAccessToken("MemberApi/Update/Mentee/BasicInfo/Success") {
                    requestFields(*requestFields)
                }
            }
        }
    }
}
