package com.koddy.server.member.application.usecase

import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@DisplayName("Member -> CompleteProfileUseCase 테스트")
internal class CompleteProfileUseCaseTest : DescribeSpec({
    val mentorRepository = mockk<MentorRepository>()
    val menteeRepository = mockk<MenteeRepository>()
    val sut = CompleteProfileUseCase(
        mentorRepository,
        menteeRepository,
    )

    describe("CompleteProfileUseCase's completeMentor") {
        context("멘토의 미완성 프로필에 대해서") {
            val mentor: Mentor = Mentor(
                MENTOR_1.platform,
                MENTOR_1.getName(),
                MENTOR_1.languages,
                MENTOR_1.universityProfile,
            ).apply(1L)
            assertSoftly(mentor) {
                introduction shouldBe null
                profileImageUrl shouldBe null
                mentoringPeriod shouldBe null
                schedules shouldBe emptyList()
                isProfileComplete shouldBe false
            }

            val command = CompleteMentorProfileCommand(
                mentor.id,
                MENTOR_1.introduction,
                MENTOR_1.profileImageUrl,
                MENTOR_1.mentoringPeriod,
                MENTOR_1.timelines,
            )
            every { mentorRepository.getById(command.mentorId) } returns mentor

            it("추가 정보를 기입한다 [자기소개, 프로필 이미지 URL, 멘토링 기간, 스케줄 정보]") {
                sut.completeMentor(command)

                verify(exactly = 1) { mentorRepository.getById(command.mentorId) }
                assertSoftly(mentor) {
                    introduction shouldBe command.introduction
                    profileImageUrl shouldBe command.profileImageUrl
                    mentoringPeriod.startDate shouldBe command.mentoringPeriod!!.startDate
                    mentoringPeriod.endDate shouldBe command.mentoringPeriod!!.endDate
                    mentoringPeriod.timeUnit shouldBe command.mentoringPeriod!!.timeUnit
                    schedules shouldHaveSize command.timelines.size
                    isProfileComplete shouldBe true
                }
            }
        }
    }

    describe("CompleteProfileUseCase's completeMentee") {
        context("멘티의 미완성 프로필에 대해서") {
            val mentee: Mentee = Mentee(
                MENTEE_1.platform,
                MENTEE_1.getName(),
                MENTEE_1.nationality,
                MENTEE_1.languages,
                MENTEE_1.interest,
            ).apply(1L)
            assertSoftly(mentee) {
                introduction shouldBe null
                profileImageUrl shouldBe null
                isProfileComplete shouldBe false
            }

            val command = CompleteMenteeProfileCommand(
                mentee.id,
                MENTEE_1.introduction,
                MENTEE_1.profileImageUrl,
            )
            every { menteeRepository.getById(command.menteeId) } returns mentee

            it("추가 정보를 기입한다 [자기소개, 프로필 이미지 URL]") {
                sut.completeMentee(command)

                verify(exactly = 1) { menteeRepository.getById(command.menteeId) }
                assertSoftly(mentee) {
                    introduction shouldBe command.introduction
                    profileImageUrl shouldBe command.profileImageUrl
                    isProfileComplete shouldBe true
                }
            }
        }
    }
})
