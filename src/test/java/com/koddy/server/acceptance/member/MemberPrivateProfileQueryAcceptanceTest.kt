package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_마이페이지_프로필을_조회한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_마이페이지_프로필을_조회한다
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 사용자 마이페이지(Private) 프로필 조회")
internal class MemberPrivateProfileQueryAcceptanceTest : AcceptanceTestKt() {
    @Nested
    @DisplayName("멘토 마이페이지(Private) 프로필 조회 API")
    internal inner class GetMentorPrivateProfile {
        @Test
        @DisplayName("멘토 마이페이지(Private) 프로필을 조회한다 (미완성 프로필)")
        fun successWithUncomplete() {
            // given
            val accessToken: String = MENTOR_1.회원가입과_로그인을_진행한다().token.accessToken

            // when - then
            멘토_마이페이지_프로필을_조회한다(accessToken)
                .statusCode(OK.value())
                .body("id", notNullValue(Long::class.java))
                .body("email", `is`(MENTOR_1.platform.email.value))
                .body("name", `is`(MENTOR_1.getName()))
                .body("profileImageUrl", nullValue())
                .body("nationality", `is`(Nationality.KOREA.code))
                .body("introduction", nullValue())
                .body("languages.main", `is`(Language.Category.KR.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.EN.code).toTypedArray()))
                .body("school", `is`(MENTOR_1.universityProfile.school))
                .body("major", `is`(MENTOR_1.universityProfile.major))
                .body("enteredIn", `is`(MENTOR_1.universityProfile.enteredIn))
                .body("authenticated", `is`(false))
                .body("period", nullValue())
                .body("schedules", empty<Int>())
                .body("role", `is`("mentor"))
                .body("profileComplete", `is`(false))
        }

        @Test
        @DisplayName("멘토 마이페이지(Private) 프로필을 조회한다 (완성 프로필)")
        fun successWithComplete() {
            // given
            val accessToken: String = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token.accessToken

            // when - then
            멘토_마이페이지_프로필을_조회한다(accessToken)
                .statusCode(OK.value())
                .body("id", notNullValue(Long::class.java))
                .body("email", `is`(MENTOR_1.platform.email.value))
                .body("name", `is`(MENTOR_1.getName()))
                .body("profileImageUrl", `is`(MENTOR_1.profileImageUrl))
                .body("nationality", `is`(Nationality.KOREA.code))
                .body("introduction", `is`(MENTOR_1.introduction))
                .body("languages.main", `is`(Language.Category.KR.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.EN.code).toTypedArray()))
                .body("school", `is`(MENTOR_1.universityProfile.school))
                .body("major", `is`(MENTOR_1.universityProfile.major))
                .body("enteredIn", `is`(MENTOR_1.universityProfile.enteredIn))
                .body("authenticated", `is`(false))
                .body("period.startDate", `is`(MENTOR_1.mentoringPeriod.startDate.toString()))
                .body("period.endDate", `is`(MENTOR_1.mentoringPeriod.endDate.toString()))
                .body("schedules.dayOfWeek", contains(*MENTOR_1.timelines.map { it.dayOfWeek.kor }.toTypedArray()))
                .body("schedules.start.hour", contains(*MENTOR_1.timelines.map { it.startTime.hour }.toTypedArray()))
                .body("schedules.start.minute", contains(*MENTOR_1.timelines.map { it.startTime.minute }.toTypedArray()))
                .body("schedules.end.hour", contains(*MENTOR_1.timelines.map { it.endTime.hour }.toTypedArray()))
                .body("schedules.end.minute", contains(*MENTOR_1.timelines.map { it.endTime.minute }.toTypedArray()))
                .body("role", `is`("mentor"))
                .body("profileComplete", `is`(true))
        }
    }

    @Nested
    @DisplayName("멘티 마이페이지(Private) 프로필 조회 API")
    internal inner class GetMenteePrivateProfile {
        @Test
        @DisplayName("멘티 마이페이지(Private) 프로필을 조회한다 (미완성 프로필)")
        fun successWithUncomplete() {
            // given
            val accessToken: String = MENTEE_1.회원가입과_로그인을_진행한다().token.accessToken

            // when - then
            멘티_마이페이지_프로필을_조회한다(accessToken)
                .statusCode(OK.value())
                .body("id", notNullValue(Long::class.java))
                .body("email", `is`(MENTEE_1.platform.email.value))
                .body("name", `is`(MENTEE_1.getName()))
                .body("profileImageUrl", nullValue())
                .body("nationality", `is`(MENTEE_1.nationality.code))
                .body("introduction", nullValue())
                .body("languages.main", `is`(Language.Category.EN.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.KR.code).toTypedArray()))
                .body("interestSchool", `is`(MENTEE_1.interest.school))
                .body("interestMajor", `is`(MENTEE_1.interest.major))
                .body("role", `is`("mentee"))
                .body("profileComplete", `is`(false))
        }

        @Test
        @DisplayName("멘티 마이페이지(Private) 프로필을 조회한다 (완성 프로필)")
        fun successWithComplete() {
            // given
            val accessToken: String = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다().token.accessToken

            // when - then
            멘티_마이페이지_프로필을_조회한다(accessToken)
                .statusCode(OK.value())
                .body("id", notNullValue(Long::class.java))
                .body("email", `is`(MENTEE_1.platform.email.value))
                .body("name", `is`(MENTEE_1.getName()))
                .body("profileImageUrl", `is`(MENTEE_1.profileImageUrl))
                .body("nationality", `is`(MENTEE_1.nationality.code))
                .body("introduction", `is`(MENTEE_1.introduction))
                .body("languages.main", `is`(Language.Category.EN.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.KR.code).toTypedArray()))
                .body("interestSchool", `is`(MENTEE_1.interest.school))
                .body("interestMajor", `is`(MENTEE_1.interest.major))
                .body("role", `is`("mentee"))
                .body("profileComplete", `is`(true))
        }
    }
}
