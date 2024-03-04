package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.LanguageFixture.EN_SUB
import com.koddy.server.common.fixture.LanguageFixture.JP_SUB
import com.koddy.server.common.fixture.LanguageFixture.KR_MAIN
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.application.usecase.query.response.MenteePublicProfile
import com.koddy.server.member.application.usecase.query.response.MentorPublicProfile
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Member -> GetMemberPublicProfileUseCase 테스트")
internal class GetMemberPublicProfileUseCaseTest : FeatureSpec({
    val memberReader = mockk<MemberReader>()
    val sut = GetMemberPublicProfileUseCase(memberReader)

    val mentorFixture = mentorFixture(id = 1L)
    val menteeFixture = menteeFixture(id = 2L)

    feature("GetMemberPublicProfileUseCase's getMentorProfile") {
        scenario("멘토 프로필을 조회한다 [미완성 -> 자기소개 & 프로필 이미지 URL & 멘토링 기간 & 스케줄]") {
            val mentor = Mentor(
                id = mentorFixture.id,
                platform = mentorFixture.platform,
                name = mentorFixture.name,
                languages = listOf(KR_MAIN.toDomain()),
                universityProfile = mentorFixture.universityProfile,
            )
            every { memberReader.getMentorWithLanguages(mentor.id) } returns mentor

            val mentorProfile: MentorPublicProfile = sut.getMentorProfile(mentor.id)

            verify(exactly = 1) { memberReader.getMentorWithLanguages(mentor.id) }
            verify(exactly = 0) { memberReader.getMenteeWithLanguages(mentor.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                name shouldBe mentor.name
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe mentor.universityProfile.school
                major shouldBe mentor.universityProfile.major
                enteredIn shouldBe mentor.universityProfile.enteredIn
                authenticated shouldBe false

                // Optional Fields
                introduction shouldBe null
                profileImageUrl shouldBe null
            }
        }

        scenario("멘토 프로필을 조회한다 [완성]") {
            val mentor: Mentor = mentorFixture.toDomainWithLanguages(listOf(KR_MAIN.toDomain()))
            every { memberReader.getMentorWithLanguages(mentor.id) } returns mentor

            val mentorProfile: MentorPublicProfile = sut.getMentorProfile(mentor.id)

            verify(exactly = 1) { memberReader.getMentorWithLanguages(mentor.id) }
            verify(exactly = 0) { memberReader.getMenteeWithLanguages(mentor.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                name shouldBe mentor.name
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe mentor.universityProfile.school
                major shouldBe mentor.universityProfile.major
                enteredIn shouldBe mentor.universityProfile.enteredIn
                authenticated shouldBe false

                // Optional Fields
                introduction shouldBe mentor.introduction
                profileImageUrl shouldBe mentor.profileImageUrl
            }
        }
    }

    feature("GetMemberPublicProfileUseCase's getMenteeProfile") {
        scenario("멘티 프로필을 조회한다 [미완성 -> 자기소개 URL & 프로필 이미지]") {
            val mentee: Mentee = Mentee(
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

            val mentorProfile: MenteePublicProfile = sut.getMenteeProfile(mentee.id)

            verify(exactly = 1) { memberReader.getMenteeWithLanguages(mentee.id) }
            verify(exactly = 0) { memberReader.getMentorWithLanguages(mentee.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                name shouldBe mentee.name
                nationality shouldBe mentee.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe mentee.interest.school
                interestMajor shouldBe mentee.interest.major

                // Optional Fields
                introduction shouldBe null
                profileImageUrl shouldBe null
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

            val mentorProfile: MenteePublicProfile = sut.getMenteeProfile(mentee.id)

            verify(exactly = 1) { memberReader.getMenteeWithLanguages(mentee.id) }
            verify(exactly = 0) { memberReader.getMentorWithLanguages(mentee.id) }
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                name shouldBe mentee.name
                nationality shouldBe mentee.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe mentee.interest.school
                interestMajor shouldBe mentee.interest.major

                // Optional Fields
                introduction shouldBe mentee.introduction
                profileImageUrl shouldBe mentee.profileImageUrl
            }
        }
    }
})
