package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Member -> CompleteProfileUseCase 테스트")
internal class CompleteProfileUseCaseTest : DescribeSpec({
    val memberReader = mockk<MemberReader>()
    val sut = CompleteProfileUseCase(memberReader)

    val mentorFixture = mentorFixture(id = 1L)
    val menteeFixture = menteeFixture(id = 2L)

    describe("CompleteProfileUseCase's completeMentor") {
        context("멘토의 미완성 프로필에 대해서") {
            val mentor = Mentor(
                id = mentorFixture.id,
                platform = mentorFixture.platform,
                name = mentorFixture.name,
                languages = mentorFixture.languages,
                universityProfile = mentorFixture.universityProfile,
            )
            assertSoftly(mentor) {
                introduction shouldBe null
                profileImageUrl shouldBe null
                mentoringPeriod shouldBe null
                schedules shouldBe emptyList()
                profileComplete shouldBe false
            }

            val command = CompleteMentorProfileCommand(
                mentorId = mentorFixture.id,
                introduction = mentorFixture.introduction,
                profileImageUrl = mentorFixture.profileImageUrl,
                mentoringPeriod = mentorFixture.mentoringPeriod,
                timelines = mentorFixture.timelines,
            )
            every { memberReader.getMentor(command.mentorId) } returns mentor

            it("추가 정보를 기입한다 [자기소개, 프로필 이미지 URL, 멘토링 기간, 스케줄 정보]") {
                sut.completeMentor(command)

                verify(exactly = 1) { memberReader.getMentor(command.mentorId) }
                assertSoftly(mentor) {
                    introduction shouldBe command.introduction
                    profileImageUrl shouldBe command.profileImageUrl
                    mentoringPeriod!!.startDate shouldBe command.mentoringPeriod!!.startDate
                    mentoringPeriod!!.endDate shouldBe command.mentoringPeriod!!.endDate
                    mentoringPeriod!!.timeUnit shouldBe command.mentoringPeriod!!.timeUnit
                    schedules shouldHaveSize command.timelines.size
                    profileComplete shouldBe true
                }
            }
        }
    }

    describe("CompleteProfileUseCase's completeMentee") {
        context("멘티의 미완성 프로필에 대해서") {
            val mentee = Mentee(
                id = menteeFixture.id,
                platform = menteeFixture.platform,
                name = menteeFixture.name,
                nationality = menteeFixture.nationality,
                languages = menteeFixture.languages,
                interest = menteeFixture.interest,
            )
            assertSoftly(mentee) {
                introduction shouldBe null
                profileImageUrl shouldBe null
                profileComplete shouldBe false
            }

            val command = CompleteMenteeProfileCommand(
                menteeId = menteeFixture.id,
                introduction = menteeFixture.introduction,
                profileImageUrl = menteeFixture.profileImageUrl,
            )
            every { memberReader.getMentee(command.menteeId) } returns mentee

            it("추가 정보를 기입한다 [자기소개, 프로필 이미지 URL]") {
                sut.completeMentee(command)

                verify(exactly = 1) { memberReader.getMentee(command.menteeId) }
                assertSoftly(mentee) {
                    introduction shouldBe command.introduction
                    profileImageUrl shouldBe command.profileImageUrl
                    profileComplete shouldBe true
                }
            }
        }
    }
})
