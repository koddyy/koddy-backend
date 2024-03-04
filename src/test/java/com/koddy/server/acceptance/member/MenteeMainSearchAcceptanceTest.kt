package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토들을_둘러본다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.커피챗_제안한_멘토를_조회한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerAllCallbackExtension
import com.koddy.server.common.fixture.MenteeFixtureStore
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
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
        private val menteeFixture: MenteeFixtureStore.MenteeFixture = menteeFixture(sequence = 1)
        private val mentorFixtures: List<MentorFixtureStore.MentorFixture> = mutableListOf<MentorFixtureStore.MentorFixture>().apply {
            (1..20).forEach { add(mentorFixture(sequence = it)) }
        }

        private val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()
        private val mentors: List<AuthMember> = mentorFixtures.map { it.회원가입과_로그인을_하고_프로필을_완성시킨다() }
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
                mentorFixtures = listOf(mentorFixtures[4], mentorFixtures[3], mentorFixtures[2]),
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
                mentorFixtures = listOf(mentorFixtures[4], mentorFixtures[2], mentorFixtures[0]),
                totalCount = 3L,
                hasNext = false,
            )
        }

        private fun assertMenteesMatch(
            response: ValidatableResponse,
            coffeeChatIds: List<Long>,
            mentorIds: List<Long>,
            mentorFixtures: List<MentorFixtureStore.MentorFixture>,
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
                    .body("result[$index].name", `is`(mentorFixtures[index].name))
                    .body("result[$index].profileImageUrl", `is`(mentorFixtures[index].profileImageUrl))
                    .body("result[$index].school", `is`(mentorFixtures[index].universityProfile.school))
                    .body("result[$index].major", `is`(mentorFixtures[index].universityProfile.major))
                    .body("result[$index].enteredIn", `is`(mentorFixtures[index].universityProfile.enteredIn))
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
                mentorFixtures = listOf(
                    mentorFixtures[19], mentorFixtures[18], mentorFixtures[17], mentorFixtures[16], mentorFixtures[15],
                    mentorFixtures[14], mentorFixtures[13], mentorFixtures[12], mentorFixtures[11], mentorFixtures[10],
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
                mentorFixtures = listOf(
                    mentorFixtures[9], mentorFixtures[8], mentorFixtures[7], mentorFixtures[6], mentorFixtures[5],
                    mentorFixtures[4], mentorFixtures[3], mentorFixtures[2], mentorFixtures[1], mentorFixtures[0],
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
                mentorFixtures = listOf(
                    mentorFixtures[18], mentorFixtures[16], mentorFixtures[14], mentorFixtures[12], mentorFixtures[10],
                    mentorFixtures[8], mentorFixtures[6], mentorFixtures[4], mentorFixtures[2], mentorFixtures[0],
                ),
                hasNext = false,
            )

            val response4: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=2&languages=KR,EN").statusCode(OK.value())
            assertMenteesMatch(
                response = response4,
                mentorIds = listOf(),
                mentorFixtures = listOf(),
                hasNext = false,
            )

            val response5: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=1&languages=KR,JP").statusCode(OK.value())
            assertMenteesMatch(
                response = response5,
                mentorIds = listOf(
                    mentors[19].id, mentors[17].id, mentors[15].id, mentors[13].id, mentors[11].id,
                    mentors[9].id, mentors[7].id, mentors[5].id, mentors[3].id, mentors[1].id,
                ),
                mentorFixtures = listOf(
                    mentorFixtures[19], mentorFixtures[17], mentorFixtures[15], mentorFixtures[13], mentorFixtures[11],
                    mentorFixtures[9], mentorFixtures[7], mentorFixtures[5], mentorFixtures[3], mentorFixtures[1],
                ),
                hasNext = false,
            )

            val response6: ValidatableResponse = 멘토들을_둘러본다("/api/mentors?page=2&languages=KR,JP").statusCode(OK.value())
            assertMenteesMatch(
                response = response6,
                mentorIds = listOf(),
                mentorFixtures = listOf(),
                hasNext = false,
            )
        }

        private fun assertMenteesMatch(
            response: ValidatableResponse,
            mentorIds: List<Long>,
            mentorFixtures: List<MentorFixtureStore.MentorFixture>,
            hasNext: Boolean,
        ) {
            response
                .body("result", hasSize<Int>(mentorFixtures.size))
                .body("hasNext", `is`(hasNext))

            mentorFixtures.indices.forEach { index ->
                response
                    .body("result[$index].id", `is`(mentorIds[index].toInt()))
                    .body("result[$index].name", `is`(mentorFixtures[index].name))
                    .body("result[$index].profileImageUrl", `is`(mentorFixtures[index].profileImageUrl))
                    .body("result[$index].school", `is`(mentorFixtures[index].universityProfile.school))
                    .body("result[$index].major", `is`(mentorFixtures[index].universityProfile.major))
                    .body("result[$index].enteredIn", `is`(mentorFixtures[index].universityProfile.enteredIn))
            }
        }
    }
}
