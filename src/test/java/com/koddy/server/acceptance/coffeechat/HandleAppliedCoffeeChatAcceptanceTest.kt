package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_거절한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_수락한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.StrategyFixture
import com.koddy.server.common.toLocalDateTime
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 멘티가 신청한 커피챗 처리")
internal class HandleAppliedCoffeeChatAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("멘티가 신청한 커피챗 거절 API")
    internal inner class Reject {
        @Test
        fun `멘토가 아니면 권한이 없다`() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-18:00".toLocalDateTime(),
                end = "2024/2/5-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_멘티의_커피챗_신청을_거절한다(
                coffeeChatId = coffeeChatId,
                accessToken = mentee.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘토는 멘티가 신청한 커피챗을 거절한다`() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-18:00".toLocalDateTime(),
                end = "2024/2/5-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_멘티의_커피챗_신청을_거절한다(
                coffeeChatId = coffeeChatId,
                accessToken = mentor.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }

    @Nested
    @DisplayName("멘티가 신청한 커피챗 수락 API")
    internal inner class Approve {
        @Test
        fun `멘토가 아니면 권한이 없다`() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-18:00".toLocalDateTime(),
                end = "2024/2/5-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_멘티의_커피챗_신청을_수락한다(
                coffeeChatId = coffeeChatId,
                fixture = StrategyFixture.KAKAO_ID,
                accessToken = mentee.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘토는 멘티가 신청한 커피챗을 수락한다`() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                start = "2024/2/5-18:00".toLocalDateTime(),
                end = "2024/2/5-18:30".toLocalDateTime(),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_멘티의_커피챗_신청을_수락한다(
                coffeeChatId = coffeeChatId,
                fixture = StrategyFixture.KAKAO_ID,
                accessToken = mentor.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }
}
