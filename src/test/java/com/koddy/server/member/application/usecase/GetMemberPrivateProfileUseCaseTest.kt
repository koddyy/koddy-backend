package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.LanguageFixture.EN_SUB
import com.koddy.server.common.fixture.LanguageFixture.JP_SUB
import com.koddy.server.common.fixture.LanguageFixture.KR_MAIN
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
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

    feature("GetMemberPrivateProfileUseCase's getMentorProfile") {
        scenario("멘토 프로필을 조회한다 [미완성 -> 자기소개 & 프로필 이미지 URL & 멘토링 기간 & 스케줄]") {
            val mentor = Mentor(
                MENTOR_1.platform,
                MENTOR_1.getName(),
                listOf(KR_MAIN.toDomain()),
                MENTOR_1.universityProfile,
            ).apply(1L)
            every { memberReader.getMentorWithLanguages(mentor.id) } returns mentor

            val mentorProfile: MentorPrivateProfile = sut.getMentorProfile(mentor.id)

            verify(exactly = 1) { memberReader.getMentorWithLanguages(mentor.id) }
            verify(exactly = 0) { memberReader.getMenteeWithLanguages(mentor.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                email shouldBe MENTOR_1.platform.email?.value
                name shouldBe MENTOR_1.getName()
                nationality shouldBe KOREA.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe MENTOR_1.universityProfile.school
                major shouldBe MENTOR_1.universityProfile.major
                enteredIn shouldBe MENTOR_1.universityProfile.enteredIn
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
            val mentor: Mentor = MENTOR_1.toDomainWithLanguages(listOf(KR_MAIN.toDomain())).apply(1L)
            every { memberReader.getMentorWithLanguages(mentor.id) } returns mentor

            val mentorProfile: MentorPrivateProfile = sut.getMentorProfile(mentor.id)

            verify(exactly = 1) { memberReader.getMentorWithLanguages(mentor.id) }
            verify(exactly = 0) { memberReader.getMenteeWithLanguages(mentor.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                email shouldBe MENTOR_1.platform.email?.value
                name shouldBe MENTOR_1.getName()
                nationality shouldBe KOREA.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe MENTOR_1.universityProfile.school
                major shouldBe MENTOR_1.universityProfile.major
                enteredIn shouldBe MENTOR_1.universityProfile.enteredIn
                authenticated shouldBe false
                role shouldBe "mentor"

                // Optional Fields
                introduction shouldBe MENTOR_1.introduction
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
                period!!.startDate shouldBe MENTOR_1.mentoringPeriod.startDate
                period!!.endDate shouldBe MENTOR_1.mentoringPeriod.endDate
                schedules.map { it.dayOfWeek } shouldContainExactly MENTOR_1.timelines.map { it.dayOfWeek.kor }
                schedules.map { it.start.hour } shouldContainExactly MENTOR_1.timelines.map { it.startTime.hour }
                schedules.map { it.start.minute } shouldContainExactly MENTOR_1.timelines.map { it.startTime.minute }
                schedules.map { it.end.hour } shouldContainExactly MENTOR_1.timelines.map { it.endTime.hour }
                schedules.map { it.end.minute } shouldContainExactly MENTOR_1.timelines.map { it.endTime.minute }

                // isCompleted
                profileComplete shouldBe true
            }
        }
    }

    feature("GetMemberPrivateProfileUseCase's getMenteeProfile") {
        scenario("멘티 프로필을 조회한다 [미완성 -> 자기소개 URL & 프로필 이미지]") {
            val mentee: Mentee = Mentee(
                MENTEE_1.platform,
                MENTEE_1.getName(),
                MENTEE_1.nationality,
                listOf(
                    KR_MAIN.toDomain(),
                    EN_SUB.toDomain(),
                    JP_SUB.toDomain(),
                ),
                MENTEE_1.interest,
            ).apply(1L)
            every { memberReader.getMenteeWithLanguages(mentee.id) } returns mentee

            val mentorProfile: MenteePrivateProfile = sut.getMenteeProfile(mentee.id)

            verify(exactly = 1) { memberReader.getMenteeWithLanguages(mentee.id) }
            verify(exactly = 0) { memberReader.getMentorWithLanguages(mentee.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                email shouldBe MENTEE_1.platform.email?.value
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe MENTEE_1.interest.school
                interestMajor shouldBe MENTEE_1.interest.major
                role shouldBe "mentee"

                // Optional Fields
                introduction shouldBe null
                profileImageUrl shouldBe null

                // isCompleted
                profileComplete shouldBe false
            }
        }

        scenario("멘티 프로필을 조회한다 [완성]") {
            val mentee: Mentee = MENTEE_1.toDomainWithLanguages(
                listOf(
                    KR_MAIN.toDomain(),
                    EN_SUB.toDomain(),
                    JP_SUB.toDomain(),
                ),
            ).apply(1L)
            every { memberReader.getMenteeWithLanguages(mentee.id) } returns mentee

            val mentorProfile: MenteePrivateProfile = sut.getMenteeProfile(mentee.id)

            verify(exactly = 1) { memberReader.getMenteeWithLanguages(mentee.id) }
            verify(exactly = 0) { memberReader.getMentorWithLanguages(mentee.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                email shouldBe MENTEE_1.platform.email?.value
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe MENTEE_1.interest.school
                interestMajor shouldBe MENTEE_1.interest.major
                role shouldBe "mentee"

                // Optional Fields
                introduction shouldBe MENTEE_1.introduction
                profileImageUrl shouldBe MENTEE_1.profileImageUrl

                // isCompleted
                profileComplete shouldBe true
            }
        }
    }
})
