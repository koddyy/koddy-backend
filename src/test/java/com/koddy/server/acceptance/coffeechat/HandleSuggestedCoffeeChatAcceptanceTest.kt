package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_거절한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.exception.MemberExceptionCode
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import java.time.LocalDateTime

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 멘토가 제안한 커피챗 처리")
internal class HandleSuggestedCoffeeChatAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("멘토가 제안한 커피챗 거절 API")
    internal inner class Reject {
        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        fun throwExceptionByInvalidPermission() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )

            // when - then
            멘티가_멘토의_커피챗_제안을_거절한다(
                coffeeChatId = coffeeChatId,
                accessToken = mentor.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        @DisplayName("멘티는 멘토가 제안한 커피챗을 거절한다")
        fun success() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )

            // when - then
            멘티가_멘토의_커피챗_제안을_거절한다(
                coffeeChatId = coffeeChatId,
                accessToken = mentee.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }

    @Nested
    @DisplayName("멘토가 제안한 커피챗 1차 수락 API")
    internal inner class Pending {
        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        fun throwExceptionByInvalidPermission() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )

            // when - then
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId = coffeeChatId,
                start = LocalDateTime.of(2024, 2, 1, 18, 0),
                end = LocalDateTime.of(2024, 2, 1, 19, 0),
                accessToken = mentor.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        @DisplayName("이미 예약되었거나 멘토링이 가능하지 않은 날짜면 예외가 발생한다")
        fun throwExceptionByCannotReservation() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )

            // when - then
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId = coffeeChatId,
                start = LocalDateTime.of(2024, 2, 1, 18, 0),
                end = LocalDateTime.of(2024, 2, 1, 19, 0),
                accessToken = mentee.token.accessToken,
            ).statusCode(HttpStatus.CONFLICT.value())
                .body("errorCode", `is`(MemberExceptionCode.CANNOT_RESERVATION.errorCode))
                .body("message", `is`(MemberExceptionCode.CANNOT_RESERVATION.message))
        }

        @Test
        @DisplayName("멘티는 멘토가 제안한 커피챗을 1차 수락한다")
        fun success() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )

            // when - then
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId = coffeeChatId,
                start = LocalDateTime.of(2024, 2, 5, 13, 0),
                end = LocalDateTime.of(2024, 2, 5, 13, 30),
                accessToken = mentee.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }
}
