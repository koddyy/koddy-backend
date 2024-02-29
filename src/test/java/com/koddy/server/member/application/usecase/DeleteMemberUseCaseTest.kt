package com.koddy.server.member.application.usecase

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
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
    val memberRepository = mockk<MemberRepository>()
    val mentorDeleter = mockk<MentorDeleter>()
    val menteeDeleter = mockk<MenteeDeleter>()
    val sut = DeleteMemberUseCase(
        memberRepository,
        mentorDeleter,
        menteeDeleter,
    )

    val mentor: Mentor = MENTOR_1.toDomain().apply(1L)
    val mentee: Mentee = MENTEE_1.toDomain().apply(2L)

    feature("DeleteMemberUseCase's invoke") {
        scenario("멘토가 서비스를 탈퇴한다") {
            every { memberRepository.getById(mentor.id) } returns mentor
            justRun { mentorDeleter.execute(mentor.id) }

            sut.invoke(mentor.id)

            verify(exactly = 1) {
                memberRepository.getById(mentor.id)
                mentorDeleter.execute(mentor.id)
            }
            verify { menteeDeleter wasNot Called }
        }

        scenario("멘티가 서비스를 탈퇴한다") {
            every { memberRepository.getById(mentee.id) } returns mentee
            justRun { menteeDeleter.execute(mentee.id) }

            sut.invoke(mentee.id)

            verify(exactly = 1) {
                memberRepository.getById(mentee.id)
                menteeDeleter.execute(mentee.id)
            }
            verify { mentorDeleter wasNot Called }
        }
    }
})
