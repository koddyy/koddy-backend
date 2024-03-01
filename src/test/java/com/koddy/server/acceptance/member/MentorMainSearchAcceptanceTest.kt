package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티들을_둘러본다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.커피챗_신청한_멘티를_조회한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerAllCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_10
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_11
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_12
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_13
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_14
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_15
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_16
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_17
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_18
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_19
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_20
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_3
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_4
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_5
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_6
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_7
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_8
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_9
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.toLocalDateTime
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
@DisplayName("[Acceptance Test] 멘토 메인 홈 조회 - 신청온 커피챗, 멘티 둘러보기")
internal class MentorMainSearchAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private var mentor: AuthMember = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
        private val mentees: List<AuthMember> = MenteeFixture.entries
            .slice(0 until 20)
            .map { it.회원가입과_로그인을_하고_프로필을_완성시킨다() }
    }

    @Nested
    @DisplayName("멘티로부터 신청온 커피챗 조회 API")
    internal inner class GetAppliedMentees {
        @Test
        fun `멘토가 아니면 권한이 없다`() {
            커피챗_신청한_멘티를_조회한다(mentees[0].token.accessToken)
                .statusCode(FORBIDDEN.value())
                .body("errorCode", `is`(INVALID_PERMISSION.errorCode))
                .body("message", `is`(INVALID_PERMISSION.message))
        }

        @Test
        fun `멘티로부터 신청온 커피챗을 조회한다 - default = Limit 3`() {
            // given
            val coffeeChats: List<Long> = listOf(
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    start = "2024/2/5-18:00".toLocalDateTime(),
                    end = "2024/2/5-18:30".toLocalDateTime(),
                    mentorId = mentor.id,
                    accessToken = mentees[0].token.accessToken,
                ),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    start = "2024/2/7-18:00".toLocalDateTime(),
                    end = "2024/2/7-18:30".toLocalDateTime(),
                    mentorId = mentor.id,
                    accessToken = mentees[1].token.accessToken,
                ),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    start = "2024/2/9-18:00".toLocalDateTime(),
                    end = "2024/2/9-18:30".toLocalDateTime(),
                    mentorId = mentor.id,
                    accessToken = mentees[2].token.accessToken,
                ),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    start = "2024/2/12-18:00".toLocalDateTime(),
                    end = "2024/2/12-18:30".toLocalDateTime(),
                    mentorId = mentor.id,
                    accessToken = mentees[3].token.accessToken,
                ),
                멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(
                    start = "2024/2/14-18:00".toLocalDateTime(),
                    end = "2024/2/14-18:30".toLocalDateTime(),
                    mentorId = mentor.id,
                    accessToken = mentees[4].token.accessToken,
                ),
            )

            /* 5명 신청 */
            val response1: ValidatableResponse = 커피챗_신청한_멘티를_조회한다(mentor.token.accessToken).statusCode(OK.value())
            assertMenteesMatch(
                response = response1,
                coffeeChatIds = listOf(coffeeChats[4], coffeeChats[3], coffeeChats[2]),
                menteeIds = listOf(mentees[4].id, mentees[3].id, mentees[2].id),
                mentees = listOf(MENTEE_5, MENTEE_4, MENTEE_3),
                totalCount = 5L,
                hasNext = true,
            )

            /* 2명 취소 */
            신청_제안한_커피챗을_취소한다(coffeeChats[3], mentees[3].token.accessToken)
            신청_제안한_커피챗을_취소한다(coffeeChats[1], mentees[1].token.accessToken)

            val response2: ValidatableResponse = 커피챗_신청한_멘티를_조회한다(mentor.token.accessToken).statusCode(OK.value())
            assertMenteesMatch(
                response = response2,
                coffeeChatIds = listOf(coffeeChats[4], coffeeChats[2], coffeeChats[0]),
                menteeIds = listOf(mentees[4].id, mentees[2].id, mentees[0].id),
                mentees = listOf(MENTEE_5, MENTEE_3, MENTEE_1),
                totalCount = 3L,
                hasNext = false,
            )
        }

        private fun assertMenteesMatch(
            response: ValidatableResponse,
            coffeeChatIds: List<Long>,
            menteeIds: List<Long>,
            mentees: List<MenteeFixture>,
            totalCount: Long,
            hasNext: Boolean,
        ) {
            response
                .body("result", hasSize<Int>(mentees.size))
                .body("totalCount", `is`(totalCount.toInt()))
                .body("hasNext", `is`(hasNext))

            mentees.indices.forEach { index ->
                response
                    .body("result[$index].coffeeChatId", `is`(coffeeChatIds[index].toInt()))
                    .body("result[$index].menteeId", `is`(menteeIds[index].toInt()))
                    .body("result[$index].name", `is`(mentees[index].getName()))
                    .body("result[$index].profileImageUrl", `is`(mentees[index].profileImageUrl))
                    .body("result[$index].nationality", `is`(mentees[index].nationality.code))
                    .body("result[$index].interestSchool", `is`(mentees[index].interest.school))
                    .body("result[$index].interestMajor", `is`(mentees[index].interest.major))
            }
        }
    }

    @Nested
    @DisplayName("멘티 둘러보기 API")
    internal inner class LookAroundMenteesByConditionQuery {
        @Test
        fun `멘티 둘러보기를 진행한다`() {
            /* 최신 가입순 */
            val response1: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=1").statusCode(OK.value())
            assertMenteesMatch(
                response = response1,
                menteeIds = listOf(
                    mentees[19].id, mentees[18].id, mentees[17].id, mentees[16].id, mentees[15].id,
                    mentees[14].id, mentees[13].id, mentees[12].id, mentees[11].id, mentees[10].id,
                ),
                mentees = listOf(
                    MENTEE_20, MENTEE_19, MENTEE_18, MENTEE_17, MENTEE_16,
                    MENTEE_15, MENTEE_14, MENTEE_13, MENTEE_12, MENTEE_11,
                ),
                hasNext = true,
            )

            val response2: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=2").statusCode(OK.value())
            assertMenteesMatch(
                response = response2,
                menteeIds = listOf(
                    mentees[9].id, mentees[8].id, mentees[7].id, mentees[6].id, mentees[5].id,
                    mentees[4].id, mentees[3].id, mentees[2].id, mentees[1].id, mentees[0].id,
                ),
                mentees = listOf(
                    MENTEE_10, MENTEE_9, MENTEE_8, MENTEE_7, MENTEE_6,
                    MENTEE_5, MENTEE_4, MENTEE_3, MENTEE_2, MENTEE_1,
                ),
                hasNext = false,
            )

            /* 최신 가입순 + 국적 */
            val response3: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=1&nationalities=EN,JP,CN").statusCode(OK.value())
            assertMenteesMatch(
                response = response3,
                menteeIds = listOf(
                    mentees[17].id, mentees[16].id, mentees[15].id, mentees[12].id, mentees[11].id,
                    mentees[10].id, mentees[7].id, mentees[6].id, mentees[5].id, mentees[2].id,
                ),
                mentees = listOf(
                    MENTEE_18, MENTEE_17, MENTEE_16, MENTEE_13, MENTEE_12,
                    MENTEE_11, MENTEE_8, MENTEE_7, MENTEE_6, MENTEE_3,
                ),
                hasNext = true,
            )

            val response4: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=2&nationalities=EN,JP,CN").statusCode(OK.value())
            assertMenteesMatch(
                response = response4,
                menteeIds = listOf(mentees[1].id, mentees[0].id),
                mentees = listOf(MENTEE_2, MENTEE_1),
                hasNext = false,
            )

            /* 최신 가입순 + 언어 */
            val response5: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=1&languages=EN,KR").statusCode(OK.value())
            assertMenteesMatch(
                response = response5,
                menteeIds = listOf(
                    mentees[18].id, mentees[16].id, mentees[14].id, mentees[12].id, mentees[10].id,
                    mentees[8].id, mentees[6].id, mentees[4].id, mentees[2].id, mentees[0].id,
                ),
                mentees = listOf(
                    MENTEE_19, MENTEE_17, MENTEE_15, MENTEE_13, MENTEE_11,
                    MENTEE_9, MENTEE_7, MENTEE_5, MENTEE_3, MENTEE_1,
                ),
                hasNext = false,
            )

            val response6: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=2&languages=KR").statusCode(OK.value())
            assertMenteesMatch(
                response = response6,
                menteeIds = listOf(),
                mentees = listOf(),
                hasNext = false,
            )

            /* 최신 가입순 + 국적 + 언어 */
            val response7: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=1&nationalities=EN,JP,CN&languages=EN,KR").statusCode(OK.value())
            assertMenteesMatch(
                response = response7,
                menteeIds = listOf(
                    mentees[16].id, mentees[12].id, mentees[10].id,
                    mentees[6].id, mentees[2].id, mentees[0].id,
                ),
                mentees = listOf(
                    MENTEE_17, MENTEE_13, MENTEE_11,
                    MENTEE_7, MENTEE_3, MENTEE_1,
                ),
                hasNext = false,
            )

            val response8: ValidatableResponse = 멘티들을_둘러본다("/api/mentees?page=2&nationalities=EN,JP,CN&languages=EN,KR").statusCode(OK.value())
            assertMenteesMatch(
                response = response8,
                menteeIds = listOf(),
                mentees = listOf(),
                hasNext = false,
            )
        }

        private fun assertMenteesMatch(
            response: ValidatableResponse,
            menteeIds: List<Long>,
            mentees: List<MenteeFixture>,
            hasNext: Boolean,
        ) {
            response
                .body("result", hasSize<Int>(mentees.size))
                .body("hasNext", `is`(hasNext))

            mentees.indices.forEach { index ->
                response
                    .body("result[$index].id", `is`(menteeIds[index].toInt()))
                    .body("result[$index].name", `is`(mentees[index].getName()))
                    .body("result[$index].profileImageUrl", `is`(mentees[index].profileImageUrl))
                    .body("result[$index].nationality", `is`(mentees[index].nationality.code))
                    .body("result[$index].interestSchool", `is`(mentees[index].interest.school))
                    .body("result[$index].interestMajor", `is`(mentees[index].interest.major))
            }
        }
    }
}
