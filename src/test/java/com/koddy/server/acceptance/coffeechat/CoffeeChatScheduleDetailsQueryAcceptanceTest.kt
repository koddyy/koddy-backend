package com.koddy.server.acceptance.coffeechat

import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.내_일정_커피챗_상세_조회를_진행한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_거절한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티의_커피챗_신청을_수락한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_1차_수락한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘티가_멘토의_커피챗_제안을_거절한다
import com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.서비스를_탈퇴한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.StrategyFixture
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Member.Status.ACTIVE
import com.koddy.server.member.domain.model.Member.Status.INACTIVE
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.OK
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 내 일정 커피챗 상세 조회")
internal class CoffeeChatScheduleDetailsQueryAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private val start: LocalDateTime = 월요일_1주차_20_00_시작.start
        private val end: LocalDateTime = 월요일_1주차_20_00_시작.end
    }

    private lateinit var mentor: AuthMember
    private lateinit var mentee: AuthMember

    @BeforeEach
    override fun setUp() {
        mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
        mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다()
    }

    @Nested
    @DisplayName("내 일정 커피챗 상세 조회를 진행한다 [MenteeFlow]")
    internal inner class GetCoffeeChatScheduleDetailsWithMenteeFlow {
        @Test
        fun `1) MENTEE_APPLY 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id, mentee.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertApplyCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertApplyCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertApplyCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `2) MENTEE_CANCEL 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id, mentee.token.accessToken)
            신청_제안한_커피챗을_취소한다(coffeeChatId, mentee.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertCancelCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertCancelCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertCancelCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `3) MENTOR_REJECT 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id, mentee.token.accessToken)
            멘토가_멘티의_커피챗_신청을_거절한다(coffeeChatId, mentor.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertRejectCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertRejectCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertRejectCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `4) MENTOR_APPROVE 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘티가_멘토에게_커피챗을_신청하고_ID를_추출한다(start, end, mentor.id, mentee.token.accessToken)
            멘토가_멘티의_커피챗_신청을_수락한다(coffeeChatId, StrategyFixture.KAKAO_ID, mentor.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertApproveCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertApproveCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertApproveCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        private fun assertApplyCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTEE_APPLY.name))
                .body("coffeeChat.applyReason", notNullValue(String::class.java))
                .body("coffeeChat.suggestReason", nullValue())
                .body("coffeeChat.cancelReason", nullValue())
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", nullValue())
                .body("coffeeChat.start", `is`(start.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.end", `is`(end.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertCancelCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(CANCEL_FROM_MENTEE_FLOW.name))
                .body("coffeeChat.applyReason", notNullValue(String::class.java))
                .body("coffeeChat.suggestReason", nullValue())
                .body("coffeeChat.cancelReason", notNullValue(String::class.java))
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", nullValue())
                .body("coffeeChat.start", `is`(start.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.end", `is`(end.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertRejectCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTOR_REJECT.name))
                .body("coffeeChat.applyReason", notNullValue(String::class.java))
                .body("coffeeChat.suggestReason", nullValue())
                .body("coffeeChat.cancelReason", nullValue())
                .body("coffeeChat.rejectReason", notNullValue(String::class.java))
                .body("coffeeChat.question", nullValue())
                .body("coffeeChat.start", `is`(start.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.end", `is`(end.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertApproveCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTOR_APPROVE.name))
                .body("coffeeChat.applyReason", notNullValue(String::class.java))
                .body("coffeeChat.suggestReason", nullValue())
                .body("coffeeChat.cancelReason", nullValue())
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", notNullValue(String::class.java))
                .body("coffeeChat.start", `is`(start.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.end", `is`(end.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.chatType", `is`(StrategyFixture.KAKAO_ID.type.value))
                .body("coffeeChat.chatValue", `is`(StrategyFixture.KAKAO_ID.value))

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }
    }

    @Nested
    @DisplayName("내 일정 커피챗 상세 조회를 진행한다 [MentorFlow]")
    internal inner class GetCoffeeChatScheduleDetailsWithMentorFlow {
        @Test
        fun `1) MENTOR_SUGGEST 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentor.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertSuggestCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertSuggestCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertSuggestCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `2) MENTOR_CANCEL 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentor.token.accessToken)
            신청_제안한_커피챗을_취소한다(coffeeChatId, mentor.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertCancelCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertCancelCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertCancelCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `3) MENTEE_REJECT 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentor.token.accessToken)
            멘티가_멘토의_커피챗_제안을_거절한다(coffeeChatId, mentee.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertRejectCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertRejectCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertRejectCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `4) MENTEE_PENDING 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentor.token.accessToken)
            멘티가_멘토의_커피챗_제안을_1차_수락한다(coffeeChatId, start, end, mentee.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertPendingCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertPendingCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertPendingCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `5) MENTOR_FINALLY_CANCEL 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentor.token.accessToken)
            멘티가_멘토의_커피챗_제안을_1차_수락한다(coffeeChatId, start, end, mentee.token.accessToken)
            멘토가_Pending_상태인_커피챗에_대해서_최종_취소를_한다(coffeeChatId, mentor.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertFinallyCancelCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertFinallyCancelCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertFinallyCancelCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        @Test
        fun `6) MENTOR_FINALLY_APPROVE 상태 커피챗 상세 조회`() {
            // given
            val coffeeChatId: Long = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id, mentor.token.accessToken)
            멘티가_멘토의_커피챗_제안을_1차_수락한다(coffeeChatId, start, end, mentee.token.accessToken)
            멘토가_Pending_상태인_커피챗에_대해서_최종_수락을_한다(coffeeChatId, StrategyFixture.KAKAO_ID, mentor.token.accessToken)

            /* Mentor 내 일정 조회 - mentee ACTIVE */
            val mentorResponse: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentor.token.accessToken).statusCode(OK.value())
            assertFinallyApproveCoffeeChatMatch(mentorResponse, coffeeChatId, mentee.id, MENTEE_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor ACTIVE */
            val menteeResponse1: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertFinallyApproveCoffeeChatMatch(menteeResponse1, coffeeChatId, mentor.id, MENTOR_1, ACTIVE)

            /* Mentee 내 일정 조회 - mentor INACTIVE */
            서비스를_탈퇴한다(mentor.token.accessToken)
            val menteeResponse2: ValidatableResponse = 내_일정_커피챗_상세_조회를_진행한다(coffeeChatId, mentee.token.accessToken).statusCode(OK.value())
            assertFinallyApproveCoffeeChatMatch(menteeResponse2, coffeeChatId, mentor.id, MENTOR_1, INACTIVE)
        }

        private fun assertSuggestCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTOR_SUGGEST.name))
                .body("coffeeChat.applyReason", nullValue())
                .body("coffeeChat.suggestReason", notNullValue(String::class.java))
                .body("coffeeChat.cancelReason", nullValue())
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", nullValue())
                .body("coffeeChat.start", nullValue())
                .body("coffeeChat.end", nullValue())
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertCancelCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(CANCEL_FROM_MENTOR_FLOW.name))
                .body("coffeeChat.applyReason", nullValue())
                .body("coffeeChat.suggestReason", notNullValue(String::class.java))
                .body("coffeeChat.cancelReason", notNullValue(String::class.java))
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", nullValue())
                .body("coffeeChat.start", nullValue())
                .body("coffeeChat.end", nullValue())
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertRejectCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTEE_REJECT.name))
                .body("coffeeChat.applyReason", nullValue())
                .body("coffeeChat.suggestReason", notNullValue(String::class.java))
                .body("coffeeChat.cancelReason", nullValue())
                .body("coffeeChat.rejectReason", notNullValue(String::class.java))
                .body("coffeeChat.question", nullValue())
                .body("coffeeChat.start", nullValue())
                .body("coffeeChat.end", nullValue())
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertPendingCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTEE_PENDING.name))
                .body("coffeeChat.applyReason", nullValue())
                .body("coffeeChat.suggestReason", notNullValue(String::class.java))
                .body("coffeeChat.cancelReason", nullValue())
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", notNullValue(String::class.java))
                .body("coffeeChat.start", `is`(start.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.end", `is`(end.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertFinallyCancelCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTOR_FINALLY_CANCEL.name))
                .body("coffeeChat.applyReason", nullValue())
                .body("coffeeChat.suggestReason", notNullValue(String::class.java))
                .body("coffeeChat.cancelReason", notNullValue(String::class.java))
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", notNullValue(String::class.java))
                .body("coffeeChat.start", `is`(start.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.end", `is`(end.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.chatType", nullValue())
                .body("coffeeChat.chatValue", nullValue())

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }

        private fun assertFinallyApproveCoffeeChatMatch(
            response: ValidatableResponse,
            coffeeChatId: Long,
            memberId: Long,
            fixture: Any,
            memberStatus: Member.Status,
        ) {
            response
                .body("coffeeChat.id", `is`(coffeeChatId.toInt()))
                .body("coffeeChat.status", `is`(MENTOR_FINALLY_APPROVE.name))
                .body("coffeeChat.applyReason", nullValue())
                .body("coffeeChat.suggestReason", notNullValue(String::class.java))
                .body("coffeeChat.cancelReason", nullValue())
                .body("coffeeChat.rejectReason", nullValue())
                .body("coffeeChat.question", notNullValue(String::class.java))
                .body("coffeeChat.start", `is`(start.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.end", `is`(end.format(ISO_LOCAL_DATE_TIME)))
                .body("coffeeChat.chatType", `is`(StrategyFixture.KAKAO_ID.type.value))
                .body("coffeeChat.chatValue", `is`(StrategyFixture.KAKAO_ID.value))

            when (fixture) {
                is MentorFixture -> assertMentorMatch(response, memberId, fixture, memberStatus)
                is MenteeFixture -> assertMenteeMatch(response, memberId, fixture, memberStatus)
            }
        }
    }

    private fun assertMentorMatch(
        response: ValidatableResponse,
        id: Long,
        fixture: MentorFixture,
        status: Member.Status,
    ) {
        response
            .body("mentor.id", `is`(id.toInt()))
            .body("mentor.name", `is`(fixture.getName()))
            .body("mentor.profileImageUrl", `is`(fixture.profileImageUrl))
            .body("mentor.introduction", `is`(fixture.introduction))
            .body("mentor.languages.main", `is`(Language.Category.KR.code))
            .body("mentor.languages.sub", containsInAnyOrder(*listOf(Language.Category.EN.code).toTypedArray()))
            .body("mentor.school", `is`(fixture.universityProfile.school))
            .body("mentor.major", `is`(fixture.universityProfile.major))
            .body("mentor.enteredIn", `is`(fixture.universityProfile.enteredIn))
            .body("mentor.status", `is`(status.name))
    }

    private fun assertMenteeMatch(
        response: ValidatableResponse,
        id: Long,
        fixture: MenteeFixture,
        status: Member.Status,
    ) {
        response
            .body("mentee.id", `is`(id.toInt()))
            .body("mentee.name", `is`(fixture.getName()))
            .body("mentee.profileImageUrl", `is`(fixture.profileImageUrl))
            .body("mentee.nationality", `is`(fixture.nationality.code))
            .body("mentee.introduction", `is`(fixture.introduction))
            .body("mentee.languages.main", `is`(Language.Category.EN.code))
            .body("mentee.languages.sub", containsInAnyOrder(*listOf(Language.Category.KR.code).toTypedArray()))
            .body("mentee.interestSchool", `is`(fixture.interest.school))
            .body("mentee.interestMajor", `is`(fixture.interest.major))
            .body("mentee.status", `is`(status.name))
    }
}
