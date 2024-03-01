package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.서비스를_탈퇴한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER
import com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 사용자 계정 관리")
internal class ManageAccountAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("회원가입 + 로그인 API")
    internal inner class SignUpAndLoginApi {
        @Test
        fun `멘토 회원가입 + 로그인 처리를 진행한다`() {
            멘토_회원가입_후_로그인을_진행한다(MENTOR_1)
                .statusCode(OK.value())
                .header(ACCESS_TOKEN_HEADER, notNullValue(String::class.java))
                .cookie(REFRESH_TOKEN_HEADER, notNullValue(String::class.java))
                .body("id", notNullValue(Long::class.java))
                .body("name", `is`(MENTOR_1.getName()))
        }

        @Test
        fun `멘티 회원가입 + 로그인 처리를 진행한다`() {
            멘티_회원가입_후_로그인을_진행한다(MENTEE_1)
                .statusCode(OK.value())
                .header(ACCESS_TOKEN_HEADER, notNullValue(String::class.java))
                .cookie(REFRESH_TOKEN_HEADER, notNullValue(String::class.java))
                .body("id", notNullValue(Long::class.java))
                .body("name", `is`(MENTEE_1.getName()))
        }
    }

    @Nested
    @DisplayName("서비스 탈퇴 API")
    internal inner class DeleteApi {
        @Test
        fun `멘토가 서비스를 탈퇴한다`() {
            // given
            val mentor: AuthMember = MENTOR_1.회원가입과_로그인을_진행한다()

            // when - then
            서비스를_탈퇴한다(mentor.token.accessToken)
                .statusCode(NO_CONTENT.value())
                .header(SET_COOKIE, containsString("$REFRESH_TOKEN_HEADER=;"))
                .header(SET_COOKIE, containsString("Max-Age=1;"))
                .cookie(REFRESH_TOKEN_HEADER, `is`(""))
        }

        @Test
        fun `멘티가 서비스를 탈퇴한다`() {
            // given
            val mentee: AuthMember = MENTEE_1.회원가입과_로그인을_진행한다()

            // when - then
            서비스를_탈퇴한다(mentee.token.accessToken)
                .statusCode(NO_CONTENT.value())
                .header(SET_COOKIE, containsString("$REFRESH_TOKEN_HEADER=;"))
                .header(SET_COOKIE, containsString("Max-Age=1;"))
                .cookie(REFRESH_TOKEN_HEADER, `is`(""))
        }
    }
}
