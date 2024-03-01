package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.자동_생성한_커피챗_링크를_삭제한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.커피챗_링크를_자동_생성한다
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import org.hamcrest.Matchers
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK

@ExtendWith(
    DatabaseCleanerEachCallbackExtension::class,
    RedisCleanerEachCallbackExtension::class,
)
@DisplayName("[Acceptance Test] 커피챗 링크 생성/삭제")
internal class ManageMeetingLinkAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("커피챗 링크 생성 API")
    internal inner class Create {
        @Test
        @DisplayName("멘티는 커피챗 링크 관련한 API에 권한이 없다")
        fun throwExceptionByInvalidPermission() {
            // given
            val accessToken: String = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token.accessToken

            // when - then
            커피챗_링크를_자동_생성한다(
                provider = "zoom",
                accessToken = accessToken,
            ).statusCode(FORBIDDEN.value())
                .body("errorCode", Matchers.`is`(INVALID_PERMISSION.errorCode))
                .body("message", Matchers.`is`(INVALID_PERMISSION.message))
        }

        @Test
        @DisplayName("제공하지 않는 Provider에 대한 커피챗 링크는 생성할 수 없다")
        fun throwExceptionByInvalidMeetingLinkProvider() {
            // given
            val accessToken: String = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token.accessToken

            // when - then
            커피챗_링크를_자동_생성한다(
                provider = "kakao",
                accessToken = accessToken,
            ).statusCode(BAD_REQUEST.value())
                .body("errorCode", Matchers.`is`(INVALID_MEETING_LINK_PROVIDER.errorCode))
                .body("message", Matchers.`is`(INVALID_MEETING_LINK_PROVIDER.message))
        }

        @Test
        @DisplayName("줌을 통해서 커피챗 링크를 생성한다")
        fun successWithZoom() {
            // given
            val accessToken: String = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token.accessToken

            // when - then
            커피챗_링크를_자동_생성한다(
                provider = "zoom",
                accessToken = accessToken,
            ).statusCode(OK.value())
                .body("id", notNullValue(String::class.java))
                .body("hostEmail", notNullValue(String::class.java))
                .body("topic", notNullValue(String::class.java))
                .body("joinUrl", notNullValue(String::class.java))
                .body("duration", notNullValue(Long::class.java))
        }
    }

    @Nested
    @DisplayName("커피챗 링크 삭제 API")
    internal inner class Delete {
        @Test
        @DisplayName("자동 생성한 커피챗 링크를 삭제한다")
        fun success() {
            // given
            val accessToken: String = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token.accessToken
            커피챗_링크를_자동_생성한다(
                provider = "zoom",
                accessToken = accessToken,
            )

            // when - then
            자동_생성한_커피챗_링크를_삭제한다(
                provider = "zoom",
                meetingId = "12345678",
                accessToken = accessToken,
            ).statusCode(NO_CONTENT.value())
        }
    }
}
