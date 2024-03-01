package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.StrategyFixture
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import java.time.LocalDateTime

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] Pending 상태인 커피챗에 대한 멘토의 최종 결정")
internal class HandlePendingCoffeeChatAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("최종 취소 API")
    internal inner class FinallyCancel {
        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        fun throwExceptionByInvalidPermission() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId = coffeeChatId,
                start = LocalDateTime.of(2024, 2, 5, 13, 0),
                end = LocalDateTime.of(2024, 2, 5, 13, 30),
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(
                coffeeChatId = coffeeChatId,
                accessToken = mentee.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        @DisplayName("멘토는 Pending 상태인 커피챗에 대해서 최종 취소한다")
        fun success() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId = coffeeChatId,
                start = LocalDateTime.of(2024, 2, 5, 13, 0),
                end = LocalDateTime.of(2024, 2, 5, 13, 30),
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(
                coffeeChatId = coffeeChatId,
                accessToken = mentor.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }

    @Nested
    @DisplayName("최종 수락 API")
    internal inner class FinallyApprove {
        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        fun throwExceptionByInvalidPermission() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId = coffeeChatId,
                start = LocalDateTime.of(2024, 2, 5, 13, 0),
                end = LocalDateTime.of(2024, 2, 5, 13, 30),
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(
                coffeeChatId = coffeeChatId,
                fixture = StrategyFixture.KAKAO_ID,
                accessToken = mentee.token.accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        @DisplayName("멘토는 Pending 상태인 커피챗에 대해서 최종 수락한다")
        fun success() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(
                menteeId = mentee.id,
                accessToken = mentor.token.accessToken,
            )
            멘티가_멘토의_커피챗_제안을_1차_수락한다(
                coffeeChatId = coffeeChatId,
                start = LocalDateTime.of(2024, 2, 5, 13, 0),
                end = LocalDateTime.of(2024, 2, 5, 13, 30),
                accessToken = mentee.token.accessToken,
            )

            // when - then
            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(
                coffeeChatId = coffeeChatId,
                fixture = StrategyFixture.KAKAO_ID,
                accessToken = mentor.token.accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }
}
