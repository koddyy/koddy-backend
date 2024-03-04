package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import com.koddy.server.member.domain.service.MenteeDeleter
import com.koddy.server.member.domain.service.MentorDeleter
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("Member -> DeleteMemberUseCase 테스트")
internal class DeleteMemberUseCaseTest : FeatureSpec({
    val memberReader = mockk<MemberReader>()
    val mentorDeleter = mockk<MentorDeleter>()
    val menteeDeleter = mockk<MenteeDeleter>()
    val sut = DeleteMemberUseCase(
        memberReader,
        mentorDeleter,
        menteeDeleter,
    )

    val mentor: Mentor = mentorFixture(id = 1L).toDomain()
    val mentee: Mentee = menteeFixture(id = 2L).toDomain()

    feature("DeleteMemberUseCase's invoke") {
        scenario("멘토가 서비스를 탈퇴한다") {
            every { memberReader.getMember(mentor.id) } returns mentor
            justRun { mentorDeleter.execute(mentor.id) }

            sut.invoke(mentor.id)

            verify(exactly = 1) {
                memberReader.getMember(mentor.id)
                mentorDeleter.execute(mentor.id)
            }
            verify { menteeDeleter wasNot Called }
        }

        scenario("멘티가 서비스를 탈퇴한다") {
            every { memberReader.getMember(mentee.id) } returns mentee
            justRun { menteeDeleter.execute(mentee.id) }

            sut.invoke(mentee.id)

            verify(exactly = 1) {
                memberReader.getMember(mentee.id)
                menteeDeleter.execute(mentee.id)
            }
            verify { mentorDeleter wasNot Called }
        }
    }
})
