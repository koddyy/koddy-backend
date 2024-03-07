package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_기본_프로필을_조회한다
import com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_기본_프로필을_조회한다
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.common.AcceptanceTestKt
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.domain.model.Language
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.OK

@ExtendWith(DatabaseCleanerEachCallbackExtension::class)
@DisplayName("[Acceptance Test] 사용자 기본(Public) 프로필 조회")
internal class MemberPublicProfileQueryAcceptanceTest : AcceptanceTestKt() {
    companion object {
        private val menteeFixture = menteeFixture(sequence = 1)
        private val mentorFixture = mentorFixture(sequence = 1)
    }

    @Nested
    @DisplayName("멘토 기본(Public) 프로필 조회 API")
    internal inner class GetMentorPublicProfile {
        @Test
        fun `멘토 기본(Public) 프로필을 조회한다 - (미완성 프로필)`() {
            // given
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘토_기본_프로필을_조회한다(mentor.id)
                .statusCode(OK.value())
                .body("id", `is`(mentor.id.toInt()))
                .body("name", `is`(mentorFixture.name))
                .body("profileImageUrl", nullValue())
                .body("introduction", nullValue())
                .body("languages.main", `is`(Language.Category.KR.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.EN.code).toTypedArray()))
                .body("school", `is`(mentorFixture.universityProfile.school))
                .body("major", `is`(mentorFixture.universityProfile.major))
                .body("enteredIn", `is`(mentorFixture.universityProfile.enteredIn))
                .body("authenticated", `is`(false))
        }

        @Test
        fun `멘토 기본(Public) 프로필을 조회한다 - (완성 프로필)`() {
            // given
            val mentor: AuthMember = mentorFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘토_기본_프로필을_조회한다(mentor.id)
                .statusCode(OK.value())
                .body("id", `is`(mentor.id.toInt()))
                .body("name", `is`(mentorFixture.name))
                .body("profileImageUrl", `is`(mentorFixture.profileImageUrl))
                .body("introduction", `is`(mentorFixture.introduction))
                .body("languages.main", `is`(Language.Category.KR.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.EN.code).toTypedArray()))
                .body("school", `is`(mentorFixture.universityProfile.school))
                .body("major", `is`(mentorFixture.universityProfile.major))
                .body("enteredIn", `is`(mentorFixture.universityProfile.enteredIn))
                .body("authenticated", `is`(false))
        }
    }

    @Nested
    @DisplayName("멘티 기본(Public) 프로필 조회 API")
    internal inner class GetMenteePublicProfile {
        @Test
        fun `멘티 기본(Public) 프로필을 조회한다 - (미완성 프로필)`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_진행한다()

            // when - then
            멘티_기본_프로필을_조회한다(mentee.id)
                .statusCode(OK.value())
                .body("id", `is`(mentee.id.toInt()))
                .body("name", `is`(menteeFixture.name))
                .body("profileImageUrl", nullValue())
                .body("nationality", `is`(menteeFixture.nationality.code))
                .body("introduction", nullValue())
                .body("languages.main", `is`(Language.Category.EN.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.KR.code).toTypedArray()))
                .body("interestSchool", `is`(menteeFixture.interest.school))
                .body("interestMajor", `is`(menteeFixture.interest.major))
        }

        @Test
        fun `멘티 기본(Public) 프로필을 조회한다 - (완성 프로필)`() {
            // given
            val mentee: AuthMember = menteeFixture.회원가입과_로그인을_하고_프로필을_완성시킨다()

            // when - then
            멘티_기본_프로필을_조회한다(mentee.id)
                .statusCode(OK.value())
                .body("id", `is`(mentee.id.toInt()))
                .body("name", `is`(menteeFixture.name))
                .body("profileImageUrl", `is`(menteeFixture.profileImageUrl))
                .body("nationality", `is`(menteeFixture.nationality.code))
                .body("introduction", `is`(menteeFixture.introduction))
                .body("languages.main", `is`(Language.Category.EN.code))
                .body("languages.sub", containsInAnyOrder(*listOf(Language.Category.KR.code).toTypedArray()))
                .body("interestSchool", `is`(menteeFixture.interest.school))
                .body("interestMajor", `is`(menteeFixture.interest.major))
        }
    }
}
