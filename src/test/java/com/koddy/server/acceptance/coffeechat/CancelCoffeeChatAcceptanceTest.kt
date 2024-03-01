package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.COFFEE_CHAT_NOT_FOUND
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.common.toLocalDateTime
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 신청/제안한 커피챗 취소")
internal class CancelCoffeeChatAcceptanceTest : AcceptanceTestKt() {
    private lateinit var mentorA: AuthMember
    private lateinit var mentorB: AuthMember
    private lateinit var menteeA: AuthMember
    private lateinit var menteeB: AuthMember

    @BeforeEach
    override fun setUp() {
        mentorA = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
        mentorB = MENTOR_2.회원가입과_로그인을_하고_프로필을_완성시킨다()
        menteeA = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
        menteeB = MENTEE_2.회원가입과_로그인을_하고_프로필을_완성시킨다()
    }

    @Nested
    @DisplayName("멘토가 제안한 커피챗 취소")
    internal inner class CancelSuggestedCoffeeChat {
        @Test
        fun `자신과 연관되지 않은 커피챗을 취소할 수 없다`() {
            // given
            val coffeeChatByMentorA: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = menteeA.id,
                accessToken = mentorA.token.accessToken,
            )
            val coffeeChatByMentorB: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = menteeA.id,
                accessToken = mentorB.token.accessToken,
            )

            // when - then
            신청_제안한_커피챗을_취소한다(
                coffeeChatId = coffeeChatByMentorA,
                accessToken = mentorB.token.accessToken,
            ).statusCode(NOT_FOUND.value())
                .body("errorCode", `is`(COFFEE_CHAT_NOT_FOUND.errorCode))
                .body("message", `is`(COFFEE_CHAT_NOT_FOUND.message))

            신청_제안한_커피챗을_취소한다(
                coffeeChatId = coffeeChatByMentorB,
                accessToken = mentorA.token.accessToken,
            ).statusCode(NOT_FOUND.value())
                .body("errorCode", `is`(COFFEE_CHAT_NOT_FOUND.errorCode))
                .body("message", `is`(COFFEE_CHAT_NOT_FOUND.message))
        }

        @Test
        fun `자신과 연관된 커피챗은 취소할 수 있다`() {
            // given
            val coffeeChatByMentorA: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = menteeA.id,
                accessToken = mentorA.token.accessToken,
            )
            val coffeeChatByMentorB: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = menteeA.id,
                accessToken = mentorB.token.accessToken,
            )

            // when - then
            신청_제안한_커피챗을_취소한다(
                coffeeChatId = coffeeChatByMentorA,
                accessToken = mentorA.token.accessToken,
            ).statusCode(NO_CONTENT.value())
            신청_제안한_커피챗을_취소한다(
                coffeeChatId = coffeeChatByMentorB,
                accessToken = mentorB.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }

    @Nested
    @DisplayName("멘티가 신청한 커피챗 취소")
    internal inner class CancelAppliedCoffeeChat {
        @Test
        fun `자신과 연관되지 않은 커피챗을 취소할 수 없다`() {
            // given
            val coffeeChatByMenteeA = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-15:00".toLocalDateTime(),
                end = "2024/2/5-15:30".toLocalDateTime(),
                mentorId = mentorA.id,
                accessToken = menteeA.token.accessToken,
            )
            val coffeeChatByMenteeB = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-17:00".toLocalDateTime(),
                end = "2024/2/5-17:30".toLocalDateTime(),
                mentorId = mentorA.id,
                accessToken = menteeB.token.accessToken,
            )

            // when - then
            신청_제안한_커피챗을_취소한다(coffeeChatByMenteeA, menteeB.token.accessToken)
                .statusCode(NOT_FOUND.value())
                .body("errorCode", `is`(COFFEE_CHAT_NOT_FOUND.errorCode))
                .body("message", `is`(COFFEE_CHAT_NOT_FOUND.message))
            신청_제안한_커피챗을_취소한다(coffeeChatByMenteeB, menteeA.token.accessToken)
                .statusCode(NOT_FOUND.value())
                .body("errorCode", `is`(COFFEE_CHAT_NOT_FOUND.errorCode))
                .body("message", `is`(COFFEE_CHAT_NOT_FOUND.message))
        }

        @Test
        fun `자신과 연관된 커피챗은 취소할 수 있다`() {
            // given
            val coffeeChatByMenteeA = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-15:00".toLocalDateTime(),
                end = "2024/2/5-15:30".toLocalDateTime(),
                mentorId = mentorA.id,
                accessToken = menteeA.token.accessToken,
            )
            val coffeeChatByMenteeB = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-17:00".toLocalDateTime(),
                end = "2024/2/5-17:30".toLocalDateTime(),
                mentorId = mentorA.id,
                accessToken = menteeB.token.accessToken,
            )

            // when - then
            신청_제안한_커피챗을_취소한다(
                coffeeChatId = coffeeChatByMenteeA,
                accessToken = menteeA.token.accessToken,
            ).statusCode(NO_CONTENT.value())
            신청_제안한_커피챗을_취소한다(
                coffeeChatId = coffeeChatByMenteeB,
                accessToken = menteeB.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }
}
