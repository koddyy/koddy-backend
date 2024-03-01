package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토들을_둘러본다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.커피챗_제안한_멘토를_조회한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerAllCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_10
import com.koddy.server.common.fixture.MentorFixture.MENTOR_11
import com.koddy.server.common.fixture.MentorFixture.MENTOR_12
import com.koddy.server.common.fixture.MentorFixture.MENTOR_13
import com.koddy.server.common.fixture.MentorFixture.MENTOR_14
import com.koddy.server.common.fixture.MentorFixture.MENTOR_15
import com.koddy.server.common.fixture.MentorFixture.MENTOR_16
import com.koddy.server.common.fixture.MentorFixture.MENTOR_17
import com.koddy.server.common.fixture.MentorFixture.MENTOR_18
import com.koddy.server.common.fixture.MentorFixture.MENTOR_19
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.common.fixture.MentorFixture.MENTOR_20
import com.koddy.server.common.fixture.MentorFixture.MENTOR_3
import com.koddy.server.common.fixture.MentorFixture.MENTOR_4
import com.koddy.server.common.fixture.MentorFixture.MENTOR_5
import com.koddy.server.common.fixture.MentorFixture.MENTOR_6
import com.koddy.server.common.fixture.MentorFixture.MENTOR_7
import com.koddy.server.common.fixture.MentorFixture.MENTOR_8
import com.koddy.server.common.fixture.MentorFixture.MENTOR_9
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerAllCallbackExtension::class)
@DisplayName("[Acceptance Test] 멘티 메인 홈 조회 - 제안온 커피챗, 멘토 둘러보기")
internal class MenteeMainSearchAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private var mentee: AuthMember = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
        private val mentors: List<AuthMember> = MentorFixture.entries
            .slice(0 until 20)
            .map { it.회원가입과_로그인을_하고_프로필을_완성시킨다() }
    }

    @Nested
    @DisplayName("멘토로부터 제안온 커피챗 조회 API")
    internal inner class GetSuggestedMentors {
        @Test
        fun `멘티가 아니면 권한이 없다`() {
            커피챗_제안한_멘토를_조회한다(mentors[0].token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘토로부터 제안온 커피챗을 조회한다 - default = Limit 3`() {
            // given
            val coffeeChats: List<Long> = listOf(
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentors[0].token.accessToken),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentors[1].token.accessToken),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentors[2].token.accessToken),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentors[3].token.accessToken),
                멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentors[4].token.accessToken),
            )

            /* 5명 제안 */
            val response1: ValidatableResponse = 커피챗_제안한_멘토를_조회한다(mentee.token.accessToken).statusCode(OK.value())
            assertMenteesMatch(
                response = response1,
                coffeeChatIds = listOf(coffeeChats[4], coffeeChats[3], coffeeChats[2]),
                mentorIds = listOf(mentors[4].id, mentors[3].id, mentors[2].id),
                mentors = listOf(MENTOR_5, MENTOR_4, MENTOR_3),
                totalCount = 5L,
                hasNext = true,
            )

            /* 2명 취소 */
            신청_제안한_커피챗을_취소한다(coffeeChats[3], mentors[3].token.accessToken)
            신청_제안한_커피챗을_취소한다(coffeeChats[1], mentors[1].token.accessToken)

            val response2: ValidatableResponse = 커피챗_제안한_멘토를_조회한다(mentee.token.accessToken).statusCode(OK.value())
            assertMenteesMatch(
                response = response2,
                coffeeChatIds = listOf(coffeeChats[4], coffeeChats[2], coffeeChats[0]),
                mentorIds = listOf(mentors[4].id, mentors[2].id, mentors[0].id),
                mentors = listOf(MENTOR_5, MENTOR_3, MENTOR_1),
                totalCount = 3L,
                hasNext = false,
            )
        }

        private fun assertMenteesMatch(
            response: ValidatableResponse,
            coffeeChatIds: List<Long>,
            mentorIds: List<Long>,
            mentors: List<MentorFixture>,
            totalCount: Long,
            hasNext: Boolean,
        ) {
            response
                .body("result", hasSize<Int>(coffeeChatIds.size))
                .body("totalCount", `is`(totalCount.toInt()))
                .body("hasNext", `is`(hasNext))

            coffeeChatIds.indices.forEach { index ->
                response
                    .body("result[$index].coffeeChatId", `is`(coffeeChatIds[index].toInt()))
                    .body("result[$index].mentorId", `is`(mentorIds[index].toInt()))
                    .body("result[$index].name", `is`(mentors[index].getName()))
                    .body("result[$index].profileImageUrl", `is`(mentors[index].profileImageUrl))
                    .body("result[$index].school", `is`(mentors[index].universityProfile.school))
                    .body("result[$index].major", `is`(mentors[index].universityProfile.major))
                    .body("result[$index].enteredIn", `is`(mentors[index].universityProfile.enteredIn))
            }
        }
    }

    @Nested
    @DisplayName("멘토 둘러보기 API")
    internal inner class LookAroundMentorsByConditionQuery {
        @Test
        fun `멘토 둘러보기를 진행한다`() {
            /* 최신 가입순 */
            val response1: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=1").statusCode(OK.value())
            assertMenteesMatch(
                response = response1,
                mentorIds = listOf(
                    mentors[19].id, mentors[18].id, mentors[17].id, mentors[16].id, mentors[15].id,
                    mentors[14].id, mentors[13].id, mentors[12].id, mentors[11].id, mentors[10].id,
                ),
                mentors = listOf(
                    MENTOR_20, MENTOR_19, MENTOR_18, MENTOR_17, MENTOR_16,
                    MENTOR_15, MENTOR_14, MENTOR_13, MENTOR_12, MENTOR_11,
                ),
                hasNext = true,
            )

            val response2: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=2").statusCode(OK.value())
            assertMenteesMatch(
                response = response2,
                mentorIds = listOf(
                    mentors[9].id, mentors[8].id, mentors[7].id, mentors[6].id, mentors[5].id,
                    mentors[4].id, mentors[3].id, mentors[2].id, mentors[1].id, mentors[0].id,
                ),
                mentors = listOf(
                    MENTOR_10, MENTOR_9, MENTOR_8, MENTOR_7, MENTOR_6,
                    MENTOR_5, MENTOR_4, MENTOR_3, MENTOR_2, MENTOR_1,
                ),
                hasNext = false,
            )

            /* 최신 가입순 + 언어 */
            val response3: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=1&languages=KR,EN").statusCode(OK.value())
            assertMenteesMatch(
                response = response3,
                mentorIds = listOf(
                    mentors[18].id, mentors[16].id, mentors[14].id, mentors[12].id, mentors[10].id,
                    mentors[8].id, mentors[6].id, mentors[4].id, mentors[2].id, mentors[0].id,
                ),
                mentors = listOf(
                    MENTOR_19, MENTOR_17, MENTOR_15, MENTOR_13, MENTOR_11,
                    MENTOR_9, MENTOR_7, MENTOR_5, MENTOR_3, MENTOR_1,
                ),
                hasNext = false,
            )

            val response4: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=2&languages=KR,EN").statusCode(OK.value())
            assertMenteesMatch(
                response = response4,
                mentorIds = listOf(),
                mentors = listOf(),
                hasNext = false,
            )

            val response5: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=1&languages=KR,JP").statusCode(OK.value())
            assertMenteesMatch(
                response = response5,
                mentorIds = listOf(
                    mentors[19].id, mentors[17].id, mentors[15].id, mentors[13].id, mentors[11].id,
                    mentors[9].id, mentors[7].id, mentors[5].id, mentors[3].id, mentors[1].id,
                ),
                mentors = listOf(
                    MENTOR_20, MENTOR_18, MENTOR_16, MENTOR_14, MENTOR_12,
                    MENTOR_10, MENTOR_8, MENTOR_6, MENTOR_4, MENTOR_2,
                ),
                hasNext = false,
            )

            val response6: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=2&languages=KR,JP").statusCode(OK.value())
            assertMenteesMatch(
                response = response6,
                mentorIds = listOf(),
                mentors = listOf(),
                hasNext = false,
            )
        }

        private fun assertMenteesMatch(
            response: ValidatableResponse,
            mentorIds: List<Long>,
            mentors: List<MentorFixture>,
            hasNext: Boolean,
        ) {
            response
                .body("result", hasSize<Int>(mentors.size))
                .body("hasNext", `is`(hasNext))

            mentors.indices.forEach { index ->
                response
                    .body("result[$index].id", `is`(mentorIds[index].toInt()))
                    .body("result[$index].name", `is`(mentors[index].getName()))
                    .body("result[$index].profileImageUrl", `is`(mentors[index].profileImageUrl))
                    .body("result[$index].school", `is`(mentors[index].universityProfile.school))
                    .body("result[$index].major", `is`(mentors[index].universityProfile.major))
                    .body("result[$index].enteredIn", `is`(mentors[index].universityProfile.enteredIn))
            }
        }
    }
}
