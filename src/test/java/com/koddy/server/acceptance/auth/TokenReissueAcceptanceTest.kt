package com.koddy.server.acceptance.auth

import com.koddy.server.acceptance.auth.AuthAcceptanceStep.토큰을_재발급받는다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken.Companion.ACCESS_TOKEN_HEADER
import com.koddy.server.auth.domain.model.AuthToken.Companion.REFRESH_TOKEN_HEADER
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MentorFixtureStore
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.NO_CONTENT

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 토큰 재발급")
internal class TokenReissueAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private val mentor: MentorFixtureStore.MentorFixture = mentorFixture(sequence = 1)
    }

    @Nested
    @DisplayName("토큰 재발급 API")
    internal inner class TokenReissueApi {
        @Test
        fun `유효한 RefreshToken을 통해서 AccessToken + RefreshToken을 재발급받는다`() {
            // given
            val member: AuthMember = mentor.회원가입과_로그인을_진행한다()

            // when - then
            토큰을_재발급받는다(member.token.refreshToken)
                .statusCode(NO_CONTENT.value())
                .header(ACCESS_TOKEN_HEADER, notNullValue(String::class.java))
                .header(SET_COOKIE, notNullValue(String::class.java))
                .cookie(REFRESH_TOKEN_HEADER, notNullValue(String::class.java))
        }
    }
}
