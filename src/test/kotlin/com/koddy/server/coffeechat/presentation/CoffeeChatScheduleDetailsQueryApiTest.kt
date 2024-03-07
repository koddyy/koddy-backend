package com.koddy.server.coffeechat.presentation

import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleDetailsUseCase
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatDetails
import com.koddy.server.coffeechat.domain.model.response.MenteeDetails
import com.koddy.server.coffeechat.domain.model.response.MentorDetails
import com.koddy.server.common.ApiDocsTestKt
import com.koddy.server.common.docs.ANY
import com.koddy.server.common.docs.ARRAY
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.NUMBER
import com.koddy.server.common.docs.OBJECT
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.fixture.CoffeeChatFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.common.mock.fake.FakeEncryptor
import com.koddy.server.global.utils.encrypt.Encryptor
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(CoffeeChatScheduleDetailsQueryApi::class)
@DisplayName("CoffeeChat -> CoffeeChatScheduleDetailsQueryApi 테스트")
internal class CoffeeChatScheduleDetailsQueryApiTest : ApiDocsTestKt() {
    @MockkBean
    private lateinit var getCoffeeChatScheduleDetailsUseCase: GetCoffeeChatScheduleDetailsUseCase
    private val encryptor: Encryptor = FakeEncryptor()

    @Nested
    @DisplayName("내 일정 커피챗 상세 조회 API [GET /api/coffeechats/{coffeeChatId}]")
    internal inner class GetCoffeeChatScheduleDetails {
        private val baseUrl = "/api/coffeechats/{coffeeChatId}"

        private val pathParameters: Array<DocumentField> = arrayOf(
            "coffeeChatId" type NUMBER means "커피챗 ID(PK)",
        )
        private val coffeeChatFields: Array<DocumentField> = arrayOf(
            "coffeeChat.id" type NUMBER means "커피챗 ID(PK)",
            "coffeeChat.status" type STRING means "커피챗 상태",
            "coffeeChat.applyReason" type ANY means "신청 이유" constraint "Nullable",
            "coffeeChat.suggestReason" type ANY means "제안 이유" constraint "Nullable",
            "coffeeChat.cancelReason" type ANY means "취소 이유" constraint "Nullable",
            "coffeeChat.rejectReason" type ANY means "거절 이유" constraint "Nullable",
            "coffeeChat.question" type ANY means "궁금한 점" constraint "Nullable",
            "coffeeChat.start" type ANY means "시작 날짜" constraint "Nullable",
            "coffeeChat.end" type ANY means "종료 날짜" constraint "Nullable",
            "coffeeChat.chatType" type ANY means "진행 방식" constraint "Nullable",
            "coffeeChat.chatValue" type ANY means "진행 방식에 대한 값" constraint "Nullable",
            "coffeeChat.createdAt" type STRING means "생성 날짜 [신청/제안한 날짜]",
            "coffeeChat.lastModifiedAt" type STRING means "마지막 수정 날짜 [정보 수정 or 상태값 변경]",
        )
        private val mentorResponseFields: Array<DocumentField> = arrayOf(
            "mentee.id" type NUMBER means "멘티 ID(PK)",
            "mentee.name" type STRING means "이름",
            "mentee.profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "mentee.nationality" type STRING means "국적" constraint "KR EN CN JP VN ETC",
            "mentee.introduction" type STRING means "자기 소개" constraint "Nullable",
            "mentee.languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "mentee.languages.main" type STRING means "메인 언어" constraint "1개",
            "mentee.languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개",
            "mentee.interestSchool" type STRING means "관심있는 학교",
            "mentee.interestMajor" type STRING means "관심있는 전공",
            "mentee.status" type STRING means "상태" constraint "ACTIVE INACTIVE BAN",
            *coffeeChatFields,
        )
        private val menteeResponseFields: Array<DocumentField> = arrayOf(
            "mentor.id" type NUMBER means "멘토 ID(PK)",
            "mentor.name" type STRING means "이름",
            "mentor.profileImageUrl" type STRING means "프로필 이미지 URL" constraint "Nullable",
            "mentor.introduction" type STRING means "자기 소개" constraint "Nullable",
            "mentor.languages" type OBJECT means "사용 가능한 언어" constraint "KR EN CN JP VN",
            "mentor.languages.main" type STRING means "메인 언어" constraint "1개",
            "mentor.languages.sub[]" type ARRAY means "서브 언어" constraint "0..N개",
            "mentor.school" type STRING means "학교",
            "mentor.major" type STRING means "전공",
            "mentor.enteredIn" type NUMBER means "학번",
            "mentor.status" type STRING means "상태" constraint "ACTIVE INACTIVE BAN",
            *coffeeChatFields,
        )

        @Test
        fun `내 일정 커피챗 상세 조회를 진행한다 (멘토 입장)`() {
            // given
            val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(
                id = 1L,
                fixture = CoffeeChatFixture.월요일_1주차_20_00_시작,
                mentor = mentor,
                mentee = mentee,
            )
            val response = MentorCoffeeChatScheduleDetails(
                mentee = MenteeDetails.from(mentee),
                coffeeChat = CoffeeChatDetails.of(coffeeChat, encryptor),
            )
            every { getCoffeeChatScheduleDetailsUseCase.invoke(any()) } returns response

            getRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentor)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/ScheduleDetails/Mentor") {
                    pathParameters(*pathParameters)
                    responseFields(*mentorResponseFields)
                }
            }
        }

        @Test
        fun `내 일정 커피챗 상세 조회를 진행한다 (멘티 입장)`() {
            val coffeeChat: CoffeeChat = MenteeFlow.applyAndApprove(
                id = 1L,
                fixture = CoffeeChatFixture.월요일_1주차_20_00_시작,
                mentee = mentee,
                mentor = mentor,
            )
            val response = MenteeCoffeeChatScheduleDetails(
                MentorDetails.from(mentor),
                CoffeeChatDetails.of(coffeeChat, encryptor),
            )
            every { getCoffeeChatScheduleDetailsUseCase.invoke(any()) } returns response

            getRequest(baseUrl, arrayOf(1L)) {
                accessToken(mentee)
            }.andExpect {
                status { isOk() }
                content { success(response) }
            }.andDo {
                makeSuccessDocsWithAccessToken("CoffeeChatApi/ScheduleDetails/Mentee") {
                    pathParameters(*pathParameters)
                    responseFields(*menteeResponseFields)
                }
            }
        }
    }
}
