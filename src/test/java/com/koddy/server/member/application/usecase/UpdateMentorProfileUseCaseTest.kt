package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand
import com.koddy.server.member.domain.model.Language.Type.MAIN
import com.koddy.server.member.domain.model.Language.Type.SUB
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Member -> UpdateMentorProfileUseCase 테스트")
internal class UpdateMentorProfileUseCaseTest : FeatureSpec({
    val memberReader = mockk<MemberReader>()
    val sut = UpdateMentorProfileUseCase(memberReader)

    val mentorFixtureA = mentorFixture(id = 1L)
    val mentorFixtureB = mentorFixture(id = 2L)

    feature("UpdateMentorProfileUseCase's updateBasicInfo") {
        val mentor: Mentor = mentorFixtureA.toDomain()

        scenario("멘토의 기본 정보를 수정한다") {
            val command = UpdateMentorBasicInfoCommand(
                mentorId = mentor.id,
                name = mentorFixtureB.name,
                profileImageUrl = mentorFixtureB.profileImageUrl,
                introduction = mentorFixtureB.introduction,
                languages = mentorFixtureB.languages,
                school = mentorFixtureB.universityProfile.school,
                major = mentorFixtureB.universityProfile.major,
                enteredIn = mentorFixtureB.universityProfile.enteredIn,
            )
            every { memberReader.getMentor(command.mentorId) } returns mentor

            sut.updateBasicInfo(command)

            verify(exactly = 1) { memberReader.getMentor(command.mentorId) }
            assertSoftly(mentor) {
                // update
                name shouldBe command.name
                introduction shouldBe command.introduction
                profileImageUrl shouldBe command.profileImageUrl
                isProfileComplete shouldBe true
                languages.filter { it.type == MAIN } shouldBe command.languages.filter { it.type == MAIN }
                languages.filter { it.type == SUB } shouldContainExactlyInAnyOrder command.languages.filter { it.type == SUB }
                universityProfile.school shouldBe command.school
                universityProfile.major shouldBe command.major
                universityProfile.enteredIn shouldBe command.enteredIn

                // keep
                platform.provider shouldBe mentorFixtureA.platform.provider
                platform.socialId shouldBe mentorFixtureA.platform.socialId
                platform.email?.value shouldBe mentorFixtureA.platform.email?.value
                nationality shouldBe Nationality.KOREA
                universityAuthentication shouldBe null
                mentoringPeriod!!.startDate shouldBe mentorFixtureA.mentoringPeriod.startDate
                mentoringPeriod!!.endDate shouldBe mentorFixtureA.mentoringPeriod.endDate
                mentoringPeriod!!.timeUnit shouldBe mentorFixtureA.mentoringPeriod.timeUnit
                schedules.map { it.timeline.dayOfWeek } shouldContainExactly mentorFixtureA.timelines.map { it.dayOfWeek }
                schedules.map { it.timeline.startTime } shouldContainExactly mentorFixtureA.timelines.map { it.startTime }
                schedules.map { it.timeline.endTime } shouldContainExactly mentorFixtureA.timelines.map { it.endTime }
                status shouldBe Member.Status.ACTIVE
                role shouldBe Role.MENTOR
            }
        }
    }

    feature("UpdateMentorProfileUseCase's updateSchedule") {
        val mentor: Mentor = mentorFixtureA.toDomain()

        scenario("멘토의 스케줄 정보를 수정한다") {
            val command = UpdateMentorScheduleCommand(
                mentor.id,
                mentorFixtureB.mentoringPeriod,
                mentorFixtureB.timelines,
            )
            every { memberReader.getMentorWithSchedules(command.mentorId) } returns mentor

            sut.updateSchedule(command)

            verify(exactly = 1) { memberReader.getMentorWithSchedules(command.mentorId) }
            assertSoftly(mentor) {
                // update
                mentoringPeriod!!.startDate shouldBe command.mentoringPeriod!!.startDate
                mentoringPeriod!!.endDate shouldBe command.mentoringPeriod!!.endDate
                mentoringPeriod!!.timeUnit shouldBe command.mentoringPeriod!!.timeUnit
                schedules.map { it.timeline.dayOfWeek } shouldContainExactlyInAnyOrder command.timelines.map { it.dayOfWeek }
                schedules.map { it.timeline.startTime } shouldContainExactly command.timelines.map { it.startTime }
                schedules.map { it.timeline.endTime } shouldContainExactly command.timelines.map { it.endTime }

                // keep
                platform.provider shouldBe mentorFixtureA.platform.provider
                platform.socialId shouldBe mentorFixtureA.platform.socialId
                platform.email?.value shouldBe mentorFixtureA.platform.email?.value
                name shouldBe mentorFixtureA.name
                nationality shouldBe Nationality.KOREA
                introduction shouldBe mentorFixtureA.introduction
                profileImageUrl shouldBe mentorFixtureA.profileImageUrl
                isProfileComplete shouldBe true
                languages.filter { it.type == MAIN } shouldBe mentorFixtureA.languages.filter { it.type == MAIN }
                languages.filter { it.type == SUB } shouldContainExactlyInAnyOrder mentorFixtureA.languages.filter { it.type == SUB }
                universityProfile.school shouldBe mentorFixtureA.universityProfile.school
                universityProfile.major shouldBe mentorFixtureA.universityProfile.major
                universityProfile.enteredIn shouldBe mentorFixtureA.universityProfile.enteredIn
                universityAuthentication shouldBe null
                status shouldBe Member.Status.ACTIVE
                role shouldBe Role.MENTOR
            }
        }
    }
})
