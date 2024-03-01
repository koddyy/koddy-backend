package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_기본_정보를_수정한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_스케줄_정보를_수정한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_기본_정보를_수정한다
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.presentation.request.model.LanguageRequestModel
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 사용자 정보 수정")
internal class UpdateProfileAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("멘토 정보 수정 API")
    internal inner class UpdateMentorProfile {
        @Nested
        @DisplayName("기본 정보 수정")
        internal inner class BasicInfo {
            @Test
            @DisplayName("멘티는 접근 권한이 없다")
            fun throwExceptionByInvalidPermission() {
                // given
                val accessToken: String = MENTEE_1.회원가입과_로그인을_진행한다().token.accessToken

                // when - then
                멘토_기본_정보를_수정한다(
                    MENTOR_2,
                    LanguageRequestModel(
                        main = Language.Category.EN.code,
                        sub = listOf(
                            Language.Category.KR.code,
                            Language.Category.CN.code,
                            Language.Category.JP.code,
                            Language.Category.VN.code,
                        ),
                    ),
                    accessToken,
                ).statusCode(FORBIDDEN.value())
                    .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                    .body("message", `is`(INVALID_PERMISSION.message))
            }

            @Test
            @DisplayName("멘토 기본 정보를 수정한다")
            fun success() {
                // given
                val accessToken: String = MENTOR_1.회원가입과_로그인을_진행한다().token.accessToken

                // when - then
                멘토_기본_정보를_수정한다(
                    MENTOR_2,
                    LanguageRequestModel(
                        main = Language.Category.EN.code,
                        sub = listOf(
                            Language.Category.KR.code,
                            Language.Category.CN.code,
                            Language.Category.JP.code,
                            Language.Category.VN.code,
                        ),
                    ),
                    accessToken,
                ).statusCode(NO_CONTENT.value())
            }
        }

        @Nested
        @DisplayName("스케줄 정보 수정")
        internal inner class Schedule {
            @Test
            @DisplayName("멘티는 접근 권한이 없다")
            fun throwExceptionByInvalidPermission() {
                // given
                val accessToken: String = MENTEE_1.회원가입과_로그인을_진행한다().token.accessToken

                // when - then
                멘토_스케줄_정보를_수정한다(
                    fixture = MENTOR_2,
                    accessToken = accessToken,
                ).statusCode(FORBIDDEN.value())
                    .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                    .body("message", `is`(INVALID_PERMISSION.message))
            }

            @Test
            @DisplayName("멘토 스케줄 정보를 수정한다")
            fun success() {
                // given
                val accessToken: String = MENTOR_1.회원가입과_로그인을_진행한다().token.accessToken

                // when - then
                멘토_스케줄_정보를_수정한다(
                    fixture = MENTOR_2,
                    accessToken = accessToken,
                ).statusCode(NO_CONTENT.value())
            }
        }
    }

    @Nested
    @DisplayName("멘티 정보 수정 API")
    internal inner class UpdateMenteeProfile {
        @Nested
        @DisplayName("기본 정보 수정")
        internal inner class BasicInfo {
            @Test
            @DisplayName("멘토는 접근 권한이 없다")
            fun throwExceptionByInvalidPermission() {
                // given
                val accessToken: String = MENTOR_1.회원가입과_로그인을_진행한다().token.accessToken

                // when - then
                멘티_기본_정보를_수정한다(
                    fixture = MENTEE_2,
                    languageRequestModel = LanguageRequestModel(
                        main = Language.Category.KR.code,
                        sub = listOf(
                            Language.Category.EN.code,
                            Language.Category.CN.code,
                            Language.Category.JP.code,
                            Language.Category.VN.code,
                        ),
                    ),
                    accessToken = accessToken,
                ).statusCode(FORBIDDEN.value())
                    .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                    .body("message", `is`(INVALID_PERMISSION.message))
            }

            @Test
            @DisplayName("멘티 기본 정보를 수정한다")
            fun success() {
                // given
                val accessToken: String = MENTEE_1.회원가입과_로그인을_진행한다().token.accessToken

                // when - then
                멘티_기본_정보를_수정한다(
                    fixture = MENTEE_2,
                    languageRequestModel = LanguageRequestModel(
                        main = Language.Category.KR.code,
                        sub = listOf(
                            Language.Category.EN.code,
                            Language.Category.CN.code,
                            Language.Category.JP.code,
                            Language.Category.VN.code,
                        ),
                    ),
                    accessToken = accessToken,
                ).statusCode(NO_CONTENT.value())
            }
        }
    }
}
