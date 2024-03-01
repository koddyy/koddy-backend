package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.OK
import java.time.LocalDateTime

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 커피챗 제안/신청")
internal class CreateCoffeeChatAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("멘토 -> 멘티 커피챗 제안 API")
    internal inner class SuggestCoffeeChat {
        @Test
        @DisplayName("멘티는 권한이 없다")
        fun throwExceptionByInvalidPermission() {
            // given
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘토가_멘티에게_커피챗을_제안한다(
                menteeId = mentee.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        @DisplayName("멘토가 멘티에게 커피챗을 제안한다")
        fun success() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘토가_멘티에게_커피챗을_제안한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            ).statusCode(OK.value())
                .body("result", notNullValue(Long::class.java))
        }
    }

    @Nested
    @DisplayName("멘티 -> 멘토 커피챗 신청 API")
    internal inner class ApplyCoffeeChat {
        @Test
        @DisplayName("멘토는 권한이 없다")
        fun throwExceptionByInvalidPermission() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = LocalDateTime.of(2024, 2, 1, 18, 0),
                end = LocalDateTime.of(2024, 2, 1, 19, 0),
                mentorId = mentor.id,
                accessToken = mentor.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        @DisplayName("이미 예약되었거나 멘토링이 가능하지 않은 날짜면 예외가 발생한다")
        fun throwExceptionByCannotReservation() {
            // given
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = LocalDateTime.of(2024, 2, 1, 18, 0),
                end = LocalDateTime.of(2024, 2, 1, 19, 0),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(CONFLICT.value())
                .body("errorCode", `is`(CANNOT_RESERVATION.errorCode))
                .body("message", `is`(CANNOT_RESERVATION.message))
        }

        @Test
        @DisplayName("멘티가 멘토에게 커피챗을 신청한다")
        fun success() {
            // given
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘티가_멘토에게_커피챗을_신청한다(
                start = LocalDateTime.of(2024, 2, 5, 13, 0),
                end = LocalDateTime.of(2024, 2, 5, 13, 30),
                mentorId = mentor.id,
                accessToken = mentee.token.accessToken,
            ).statusCode(OK.value())
                .body("result", notNullValue(Long::class.java))
        }
    }
}
