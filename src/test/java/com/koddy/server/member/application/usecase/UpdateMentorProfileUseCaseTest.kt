package com.koddy.server.member.application.usecase

import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand
import com.koddy.server.member.domain.model.Language.Type.MAIN
import com.koddy.server.member.domain.model.Language.Type.SUB
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MentorRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@DisplayName("Member -> UpdateMentorProfileUseCase 테스트")
internal class UpdateMentorProfileUseCaseTest : FeatureSpec({
    val mentorRepository = mockk<MentorRepository>()
    val sut = UpdateMentorProfileUseCase(mentorRepository)

    feature("UpdateMentorProfileUseCase's updateBasicInfo") {
        val mentor: Mentor = MENTOR_1.toDomain().apply(1L)

        scenario("멘토의 기본 정보를 수정한다") {
            val command = UpdateMentorBasicInfoCommand(
                mentor.id,
                MENTOR_2.getName(),
                MENTOR_2.profileImageUrl,
                MENTOR_2.introduction,
                MENTOR_2.languages,
                MENTOR_2.universityProfile.school,
                MENTOR_2.universityProfile.major,
                MENTOR_2.universityProfile.enteredIn,
            )
            every { mentorRepository.getById(command.mentorId) } returns mentor

            sut.updateBasicInfo(command)

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
                platform.provider shouldBe MENTOR_1.platform.provider
                platform.socialId shouldBe MENTOR_1.platform.socialId
                platform.email.value shouldBe MENTOR_1.platform.email.value
                nationality shouldBe Nationality.KOREA
                universityAuthentication shouldBe null
                mentoringPeriod.startDate shouldBe MENTOR_1.mentoringPeriod.startDate
                mentoringPeriod.endDate shouldBe MENTOR_1.mentoringPeriod.endDate
                mentoringPeriod.timeUnit shouldBe MENTOR_1.mentoringPeriod.timeUnit
                schedules.map { it.timeline.dayOfWeek } shouldContainExactlyInAnyOrder MENTOR_1.timelines.map { it.dayOfWeek }
                status shouldBe Member.Status.ACTIVE
                role shouldBe Role.MENTOR
            }
        }
    }

    feature("UpdateMentorProfileUseCase's updateSchedule") {
        val mentor: Mentor = MENTOR_1.toDomain().apply(1L)

        scenario("멘토의 스케줄 정보를 수정한다") {
            val command = UpdateMentorScheduleCommand(
                mentor.id,
                MENTOR_2.mentoringPeriod,
                MENTOR_2.timelines,
            )
            every { mentorRepository.getById(command.mentorId) } returns mentor

            sut.updateSchedule(command)

            assertSoftly(mentor) {
                // update
                mentoringPeriod.startDate shouldBe command.mentoringPeriod!!.startDate
                mentoringPeriod.endDate shouldBe command.mentoringPeriod!!.endDate
                mentoringPeriod.timeUnit shouldBe command.mentoringPeriod!!.timeUnit
                schedules.map { it.timeline.dayOfWeek } shouldContainExactlyInAnyOrder command.timelines.map { it.dayOfWeek }

                // keep
                platform.provider shouldBe MENTOR_1.platform.provider
                platform.socialId shouldBe MENTOR_1.platform.socialId
                platform.email.value shouldBe MENTOR_1.platform.email.value
                name shouldBe MENTOR_1.getName()
                nationality shouldBe Nationality.KOREA
                introduction shouldBe MENTOR_1.introduction
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
                isProfileComplete shouldBe true
                languages.filter { it.type == MAIN } shouldBe MENTOR_1.languages.filter { it.type == MAIN }
                languages.filter { it.type == SUB } shouldContainExactlyInAnyOrder MENTOR_1.languages.filter { it.type == SUB }
                universityProfile.school shouldBe MENTOR_1.universityProfile.school
                universityProfile.major shouldBe MENTOR_1.universityProfile.major
                universityProfile.enteredIn shouldBe MENTOR_1.universityProfile.enteredIn
                universityAuthentication shouldBe null
                status shouldBe Member.Status.ACTIVE
                role shouldBe Role.MENTOR
            }
        }
    }
})
