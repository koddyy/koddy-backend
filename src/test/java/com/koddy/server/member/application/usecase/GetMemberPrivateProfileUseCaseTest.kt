package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.LanguageFixture.EN_SUB
import com.koddy.server.common.fixture.LanguageFixture.JP_SUB
import com.koddy.server.common.fixture.LanguageFixture.KR_MAIN
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.application.usecase.query.response.MenteePrivateProfile
import com.koddy.server.member.application.usecase.query.response.MentorPrivateProfile
import com.koddy.server.member.domain.model.Nationality.KOREA
import com.koddy.server.member.domain.model.mentee.Mentee
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
@DisplayName("Member -> GetMemberPrivateProfileUseCase 테스트")
internal class GetMemberPrivateProfileUseCaseTest : FeatureSpec({
    val memberReader = mockk<MemberReader>()
    val sut = GetMemberPrivateProfileUseCase(memberReader)

    val mentorFixture = mentorFixture(id = 1L)
    val menteeFixture = menteeFixture(id = 2L)

    feature("GetMemberPrivateProfileUseCase's getMentorProfile") {
        scenario("멘토 프로필을 조회한다 [미완성 -> 자기소개 & 프로필 이미지 URL & 멘토링 기간 & 스케줄]") {
            val mentor = Mentor(
                id = mentorFixture.id,
                platform = mentorFixture.platform,
                name = mentorFixture.name,
                languages = listOf(KR_MAIN.toDomain()),
                universityProfile = mentorFixture.universityProfile,
            )
            every { memberReader.getMentorWithLanguages(mentor.id) } returns mentor

            val mentorProfile: MentorPrivateProfile = sut.getMentorProfile(mentor.id)

            verify(exactly = 1) { memberReader.getMentorWithLanguages(mentor.id) }
            verify(exactly = 0) { memberReader.getMenteeWithLanguages(mentor.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                email shouldBe mentor.platform.email?.value
                name shouldBe mentor.name
                nationality shouldBe KOREA.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe mentor.universityProfile.school
                major shouldBe mentor.universityProfile.major
                enteredIn shouldBe mentor.universityProfile.enteredIn
                authenticated shouldBe false
                role shouldBe "mentor"

                // Optional Fields
                introduction shouldBe null
                profileImageUrl shouldBe null
                period shouldBe null
                schedules shouldBe emptyList()

                // isCompleted
                profileComplete shouldBe false
            }
        }

        scenario("멘토 프로필을 조회한다 [완성]") {
            val mentor: Mentor = mentorFixture.toDomainWithLanguages(listOf(KR_MAIN.toDomain()))
            every { memberReader.getMentorWithLanguages(mentor.id) } returns mentor

            val mentorProfile: MentorPrivateProfile = sut.getMentorProfile(mentor.id)

            verify(exactly = 1) { memberReader.getMentorWithLanguages(mentor.id) }
            verify(exactly = 0) { memberReader.getMenteeWithLanguages(mentor.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                email shouldBe mentor.platform.email?.value
                name shouldBe mentor.name
                nationality shouldBe KOREA.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe mentor.universityProfile.school
                major shouldBe mentor.universityProfile.major
                enteredIn shouldBe mentor.universityProfile.enteredIn
                authenticated shouldBe false
                role shouldBe "mentor"

                // Optional Fields
                introduction shouldBe mentor.introduction
                profileImageUrl shouldBe mentor.profileImageUrl
                period!!.startDate shouldBe mentor.mentoringPeriod?.startDate
                period!!.endDate shouldBe mentor.mentoringPeriod?.endDate
                schedules.map { it.dayOfWeek } shouldContainExactly mentor.schedules.map { it.timeline.dayOfWeek.kor }
                schedules.map { it.start.hour } shouldContainExactly mentor.schedules.map { it.timeline.startTime.hour }
                schedules.map { it.start.minute } shouldContainExactly mentor.schedules.map { it.timeline.startTime.minute }
                schedules.map { it.end.hour } shouldContainExactly mentor.schedules.map { it.timeline.endTime.hour }
                schedules.map { it.end.minute } shouldContainExactly mentor.schedules.map { it.timeline.endTime.minute }

                // isCompleted
                profileComplete shouldBe true
            }
        }
    }

    feature("GetMemberPrivateProfileUseCase's getMenteeProfile") {
        scenario("멘티 프로필을 조회한다 [미완성 -> 자기소개 URL & 프로필 이미지]") {
            val mentee = Mentee(
                id = menteeFixture.id,
                platform = menteeFixture.platform,
                name = menteeFixture.name,
                nationality = menteeFixture.nationality,
                languages = listOf(
                    KR_MAIN.toDomain(),
                    EN_SUB.toDomain(),
                    JP_SUB.toDomain(),
                ),
                interest = menteeFixture.interest,
            )
            every { memberReader.getMenteeWithLanguages(mentee.id) } returns mentee

            val mentorProfile: MenteePrivateProfile = sut.getMenteeProfile(mentee.id)

            verify(exactly = 1) { memberReader.getMenteeWithLanguages(mentee.id) }
            verify(exactly = 0) { memberReader.getMentorWithLanguages(mentee.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                email shouldBe mentee.platform.email?.value
                name shouldBe mentee.name
                nationality shouldBe mentee.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe mentee.interest.school
                interestMajor shouldBe mentee.interest.major
                role shouldBe "mentee"

                // Optional Fields
                introduction shouldBe null
                profileImageUrl shouldBe null

                // isCompleted
                profileComplete shouldBe false
            }
        }

        scenario("멘티 프로필을 조회한다 [완성]") {
            val mentee: Mentee = menteeFixture.toDomainWithLanguages(
                listOf(
                    KR_MAIN.toDomain(),
                    EN_SUB.toDomain(),
                    JP_SUB.toDomain(),
                ),
            )
            every { memberReader.getMenteeWithLanguages(mentee.id) } returns mentee

            val mentorProfile: MenteePrivateProfile = sut.getMenteeProfile(mentee.id)

            verify(exactly = 1) { memberReader.getMenteeWithLanguages(mentee.id) }
            verify(exactly = 0) { memberReader.getMentorWithLanguages(mentee.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                email shouldBe mentee.platform.email?.value
                name shouldBe mentee.name
                nationality shouldBe mentee.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe mentee.interest.school
                interestMajor shouldBe mentee.interest.major
                role shouldBe "mentee"

                // Optional Fields
                introduction shouldBe mentee.introduction
                profileImageUrl shouldBe mentee.profileImageUrl

                // isCompleted
                profileComplete shouldBe true
            }
        }
    }
})
