package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_프로필을_완성시킨다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_프로필을_완성시킨다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 사용자 프로필 완성")
internal class CompleteProfileAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("멘토 프로필 완성 API")
    internal inner class CompleteMentor {
        @Test
        fun `멘토가 아니면 권한이 없다`() {
            // given
            val member: AuthMember = MENTEE_1.회원가입과_로그인을_진행한다()

            // when - then
            멘토_프로필을_완성시킨다(MENTOR_1, member.token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘토의 프로필을 완성한다 (자기소개, 프로필 이미지 URL, 멘토링 기간, 스케줄 정보)`() {
            // given
            val member: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            멘토_프로필을_완성시킨다(MENTOR_1, member.token.accessToken).statusCode(NO_CONTENT.value())
        }
    }

    @Nested
    @DisplayName("멘티 프로필 완성 API")
    internal inner class CompleteMentee {
        @Test
        fun `멘티가 아니면 권한이 없다`() {
            // given
            val member: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            멘티_프로필을_완성시킨다(MENTEE_1, member.token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘티의 프로필을 완성한다 (자기소개, 프로필 이미지 URL)`() {
            // given
            val member: AuthMember = MENTEE_1.회원가입과_로그인을_진행한다()

            // when - then
            멘티_프로필을_완성시킨다(MENTEE_1, member.token.accessToken).statusCode(NO_CONTENT.value())
        }
    }
}
