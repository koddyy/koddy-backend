package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.LanguageFixture.EN_SUB
import com.koddy.server.common.fixture.LanguageFixture.JP_SUB
import com.koddy.server.common.fixture.LanguageFixture.KR_MAIN
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.application.usecase.query.response.MenteePublicProfile
import com.koddy.server.member.application.usecase.query.response.MentorPublicProfile
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@UnitTestKt
@DisplayName("Member -> GetMemberPublicProfileUseCase 테스트")
internal class GetMemberPublicProfileUseCaseTest : FeatureSpec({
    val mentorRepository = mockk<MentorRepository>()
    val menteeRepository = mockk<MenteeRepository>()
    val sut = GetMemberPublicProfileUseCase(
        mentorRepository,
        menteeRepository,
    )

    feature("GetMemberPublicProfileUseCase's getMentorProfile") {
        scenario("멘토 프로필을 조회한다 [미완성 -> 자기소개 & 프로필 이미지 URL & 멘토링 기간 & 스케줄]") {
            val mentor = Mentor(
                MENTOR_1.platform,
                MENTOR_1.getName(),
                listOf(KR_MAIN.toDomain()),
                MENTOR_1.universityProfile,
            ).apply(1L)
            every { mentorRepository.getProfile(mentor.id) } returns mentor

            val mentorProfile: MentorPublicProfile = sut.getMentorProfile(mentor.id)
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                name shouldBe MENTOR_1.getName()
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe MENTOR_1.universityProfile.school
                major shouldBe MENTOR_1.universityProfile.major
                enteredIn shouldBe MENTOR_1.universityProfile.enteredIn
                authenticated shouldBe false

                // Optional Fields
                introduction shouldBe null
                profileImageUrl shouldBe null
            }
        }

        scenario("멘토 프로필을 조회한다 [완성]") {
            val mentor: Mentor = MENTOR_1.toDomainWithLanguages(listOf(KR_MAIN.toDomain())).apply(1L)
            every { mentorRepository.getProfile(mentor.id) } returns mentor

            val mentorProfile: MentorPublicProfile = sut.getMentorProfile(mentor.id)
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentor.id
                name shouldBe MENTOR_1.getName()
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldBe emptyList()
                school shouldBe MENTOR_1.universityProfile.school
                major shouldBe MENTOR_1.universityProfile.major
                enteredIn shouldBe MENTOR_1.universityProfile.enteredIn
                authenticated shouldBe false

                // Optional Fields
                introduction shouldBe MENTOR_1.introduction
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
            }
        }
    }

    feature("GetMemberPublicProfileUseCase's getMenteeProfile") {
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
            every { menteeRepository.getProfile(mentee.id) } returns mentee

            val mentorProfile: MenteePublicProfile = sut.getMenteeProfile(mentee.id)
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe MENTEE_1.interest.school
                interestMajor shouldBe MENTEE_1.interest.major

                // Optional Fields
                introduction shouldBe null
                profileImageUrl shouldBe null
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
            every { menteeRepository.getProfile(mentee.id) } returns mentee

            val mentorProfile: MenteePublicProfile = sut.getMenteeProfile(mentee.id)
            assertSoftly(mentorProfile) {
                // Required Fields
                id shouldBe mentee.id
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality.code
                languages.main shouldBe KR_MAIN.category.code
                languages.sub shouldContainExactlyInAnyOrder listOf(
                    EN_SUB.category.code,
                    JP_SUB.category.code,
                )
                interestSchool shouldBe MENTEE_1.interest.school
                interestMajor shouldBe MENTEE_1.interest.major

                // Optional Fields
                introduction shouldBe MENTEE_1.introduction
                profileImageUrl shouldBe MENTEE_1.profileImageUrl
            }
        }
    }
})
