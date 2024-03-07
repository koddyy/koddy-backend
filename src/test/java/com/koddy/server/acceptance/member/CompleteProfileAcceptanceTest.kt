package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_프로필을_완성시킨다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_프로필을_완성시킨다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
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
    companion object {
        private val menteeFixture = menteeFixture(sequence = 1)
        private val mentorFixture = mentorFixture(sequence = 1)
    }

    @Nested
    @DisplayName("멘토 프로필 완성 API")
    internal inner class CompleteMentor {
        @Test
        fun `멘토가 아니면 권한이 없다`() {
            // given
            val member: AuthMember = menteeFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토_프로필을_완성시킨다(mentorFixture, member.token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘토의 프로필을 완성한다 (자기소개, 프로필 이미지 URL, 멘토링 기간, 스케줄 정보)`() {
            // given
            val member: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토_프로필을_완성시킨다(mentorFixture, member.token.accessToken).statusCode(NO_CONTENT.value())
        }
    }

    @Nested
    @DisplayName("멘티 프로필 완성 API")
    internal inner class CompleteMentee {
        @Test
        fun `멘티가 아니면 권한이 없다`() {
            // given
            val member: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘티_프로필을_완성시킨다(menteeFixture, member.token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘티의 프로필을 완성한다 (자기소개, 프로필 이미지 URL)`() {
            // given
            val member: AuthMember = menteeFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘티_프로필을_완성시킨다(menteeFixture, member.token.accessToken).statusCode(NO_CONTENT.value())
        }
    }
}
