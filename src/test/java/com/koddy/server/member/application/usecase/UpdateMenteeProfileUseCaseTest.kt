package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand
import com.koddy.server.member.domain.model.Language.Type.MAIN
import com.koddy.server.member.domain.model.Language.Type.SUB
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentee.Mentee
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
@DisplayName("Member -> UpdateMenteeProfileUseCase 테스트")
internal class UpdateMenteeProfileUseCaseTest : FeatureSpec({
    val memberReader = mockk<MemberReader>()
    val sut = UpdateMenteeProfileUseCase(memberReader)

    val menteeFixtureA = menteeFixture(id = 1L)
    val menteeFixtureB = menteeFixture(id = 2L)

    feature("UpdateMenteeProfileUseCase's updateBasicInfo") {
        val mentee: Mentee = menteeFixtureA.toDomain()

        scenario("멘티의 기본 정보를 수정한다") {
            val command = UpdateMenteeBasicInfoCommand(
                menteeId = mentee.id,
                name = menteeFixtureB.name,
                nationality = menteeFixtureB.nationality,
                profileImageUrl = menteeFixtureB.profileImageUrl,
                introduction = menteeFixtureB.introduction,
                languages = menteeFixtureB.languages,
                interestSchool = menteeFixtureB.interest.school,
                interestMajor = menteeFixtureB.interest.major,
            )
            every { memberReader.getMentee(command.menteeId) } returns mentee

            sut.updateBasicInfo(command)

            verify(exactly = 1) { memberReader.getMentee(command.menteeId) }
            assertSoftly(mentee) {
                // update
                name shouldBe command.name
                nationality shouldBe command.nationality
                introduction shouldBe command.introduction
                profileImageUrl shouldBe command.profileImageUrl
                isProfileComplete shouldBe true
                languages.filter { it.type == MAIN } shouldBe command.languages.filter { it.type == MAIN }
                languages.filter { it.type == SUB } shouldContainExactlyInAnyOrder command.languages.filter { it.type == SUB }
                interest.school shouldBe command.interestSchool
                interest.major shouldBe command.interestMajor

                // keep
                platform.provider shouldBe menteeFixtureA.platform.provider
                platform.socialId shouldBe menteeFixtureA.platform.socialId
                platform.email?.value shouldBe menteeFixtureA.platform.email?.value
                status shouldBe Member.Status.ACTIVE
                role shouldBe Role.MENTEE
            }
        }
    }
})
