package com.koddy.server.acceptance.auth

import com.koddy.server.acceptance.auth.AuthAcceptanceStep.Google_OAuth_로그인을_진행한다
import com.koddy.server.acceptance.auth.AuthAcceptanceStep.Google_OAuth_인증_URL를_생성한다
import com.koddy.server.acceptance.auth.AuthAcceptanceStep.로그아웃을_진행한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken.Companion.ACCESS_TOKEN_HEADER
import com.koddy.server.auth.domain.model.AuthToken.Companion.REFRESH_TOKEN_HEADER
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MentorFixtureStore
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.utils.OAuthDummy.GOOGLE_PROVIDER
import com.koddy.server.common.utils.OAuthDummy.REDIRECT_URI
import com.koddy.server.common.utils.OAuthDummy.STATE
import com.koddy.server.common.utils.OAuthDummy.mentorAuthorizationCode
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] OAuth 인증 관련")
internal class OAuthAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private val mentor: MentorFixtureStore.MentorFixture = mentorFixture(id = 1L)
    }

    @Nested
    @DisplayName("OAuth 인증 URL 요청 API")
    internal inner class QueryOAuthLink {
        @Test
        fun `Google OAuth 인증 URL을 요청한다`() {
            Google_OAuth_인증_URL를_생성한다(
                oAuthProvider = GOOGLE_PROVIDER,
                redirectUri = REDIRECT_URI,
            ).statusCode(OK.value())
                .body("result", notNullValue(String::class.java))
        }
    }

    @Nested
    @DisplayName("OAuth 로그인 API")
    internal inner class OAuthLoginApi {
        @Test
        fun `DB에 이메일에 대한 사용자 정보가 없으면 OAuth UserInfo를 토대로 회원가입을 진행한다`() {
            Google_OAuth_로그인을_진행한다(
                oAuthProvider = GOOGLE_PROVIDER,
                authorizationCode = mentorAuthorizationCode(mentor.id),
                redirectUri = REDIRECT_URI,
                state = STATE,
            ).statusCode(NOT_FOUND.value())
                .body("id", `is`(mentor.platform.socialId))
                .body("name", `is`(mentor.name))
                .body("email", `is`(mentor.platform.email?.value))
                .body("profileImageUrl", `is`(mentor.profileImageUrl))
        }

        @Test
        fun `DB에 이메일에 대한 사용자 정보가 있으면 로그인을 진행하고 Token을 발급받는다`() {
            // given
            mentor.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            Google_OAuth_로그인을_진행한다(
                oAuthProvider = GOOGLE_PROVIDER,
                authorizationCode = mentorAuthorizationCode(mentor.id),
                redirectUri = REDIRECT_URI,
                state = STATE,
            ).statusCode(OK.value())
                .header(ACCESS_TOKEN_HEADER, notNullValue(String::class.java))
                .header(SET_COOKIE, notNullValue(String::class.java))
                .cookie(REFRESH_TOKEN_HEADER, notNullValue(String::class.java))
                .body("id", notNullValue(Long::class.java))
                .body("name", `is`(mentor.name))
        }
    }

    @Nested
    @DisplayName("로그아웃 API")
    internal inner class LogoutApi {
        @Test
        fun `로그아웃을 진행한다`() {
            // given
            val member: AuthMember = mentor.회원가입과_로그인을_진행한다()

            // when - then
            로그아웃을_진행한다(member.token.accessToken)
                .statusCode(NO_CONTENT.value())
                .header(SET_COOKIE, containsString("$REFRESH_TOKEN_HEADER=;"))
                .header(SET_COOKIE, containsString("Max-Age=1;"))
                .cookie(REFRESH_TOKEN_HEADER, `is`(""))
        }
    }
}
