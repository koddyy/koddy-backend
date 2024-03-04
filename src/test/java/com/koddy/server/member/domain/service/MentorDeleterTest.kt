package com.koddy.server.member.domain.service

import com.koddy.server.common.IntegrateTestKt
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.member.domain.model.AvailableLanguage
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.mentor.Schedule
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.MentorRepository
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FOUND
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

@IntegrateTestKt
@io.kotest.core.annotation.DisplayName("Member -> MentorDeleter 테스트 [IntegrateTest]")
internal class MentorDeleterTest(
    private val sut: MentorDeleter,
    private val memberRepository: MemberRepository,
    private val mentorRepository: MentorRepository,
    private val em: EntityManager,
) {
    companion object {
        private val mentorFixture = mentorFixture(sequence = 1)
    }

    @Test
    fun `멘토를 삭제한다 (Soft Delete)`() {
        // given
        val mentor: Member<*> = memberRepository.save(mentorFixture.toDomain())
        assertSoftly {
            getLanguage(mentor.id) shouldHaveSize mentorFixture.languages.size
            getSchedule(mentor.id) shouldHaveSize mentorFixture.timelines.size
            getMentorByJpql(mentor.id) shouldNotBe null
            getMemberByJpql(mentor.id) shouldNotBe null
            getMentorByNative(mentor.id) shouldNotBe null
            getMemberByNative(mentor.id) shouldNotBe null

            assertSoftly(getMentorByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe mentorFixture.platform.provider
                name shouldBe mentorFixture.name
                nationality shouldBe Nationality.KOREA
                introduction shouldBe mentorFixture.introduction
                profileImageUrl shouldBe mentorFixture.profileImageUrl
                role shouldBe Role.MENTOR
                universityProfile.school shouldBe mentorFixture.universityProfile.school
                universityProfile.major shouldBe mentorFixture.universityProfile.major
                universityProfile.enteredIn shouldBe mentorFixture.universityProfile.enteredIn
                universityAuthentication shouldBe null
                mentoringPeriod!!.startDate shouldBe mentorFixture.mentoringPeriod.startDate
                mentoringPeriod!!.endDate shouldBe mentorFixture.mentoringPeriod.endDate
                mentoringPeriod!!.timeUnit shouldBe mentorFixture.mentoringPeriod.timeUnit

                // Effected
                platform.socialId shouldBe mentorFixture.platform.socialId
                platform.email?.value shouldBe mentorFixture.platform.email?.value
                status shouldBe Member.Status.ACTIVE
                profileComplete shouldBe true
            }

            assertSoftly(getMemberByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe mentorFixture.platform.provider
                name shouldBe mentorFixture.name
                nationality shouldBe Nationality.KOREA
                introduction shouldBe mentorFixture.introduction
                profileImageUrl shouldBe mentorFixture.profileImageUrl
                role shouldBe Role.MENTOR

                // Effected
                platform.socialId shouldBe mentorFixture.platform.socialId
                platform.email?.value shouldBe mentorFixture.platform.email?.value
                status shouldBe Member.Status.ACTIVE
                profileComplete shouldBe true
            }
        }

        // when
        sut.execute(mentor.id)

        // then
        assertSoftly {
            getLanguage(mentor.id) shouldHaveSize mentorFixture.languages.size
            getSchedule(mentor.id) shouldHaveSize 0
            shouldThrow<MemberException> {
                getMentorByJpql(mentor.id)
                getMemberByJpql(mentor.id)
            } shouldHaveMessage MENTOR_NOT_FOUND.message
            getMentorByNative(mentor.id) shouldNotBe null
            getMemberByNative(mentor.id) shouldNotBe null

            assertSoftly(getMentorByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe mentorFixture.platform.provider
                name shouldBe mentorFixture.name
                nationality shouldBe Nationality.KOREA
                introduction shouldBe mentorFixture.introduction
                profileImageUrl shouldBe mentorFixture.profileImageUrl
                role shouldBe Role.MENTOR
                universityProfile.school shouldBe mentorFixture.universityProfile.school
                universityProfile.major shouldBe mentorFixture.universityProfile.major
                universityProfile.enteredIn shouldBe mentorFixture.universityProfile.enteredIn
                universityAuthentication shouldBe null
                mentoringPeriod!!.startDate shouldBe mentorFixture.mentoringPeriod.startDate
                mentoringPeriod!!.endDate shouldBe mentorFixture.mentoringPeriod.endDate
                mentoringPeriod!!.timeUnit shouldBe mentorFixture.mentoringPeriod.timeUnit

                // Effected
                platform.socialId shouldBe null
                platform.email shouldBe null
                status shouldBe Member.Status.INACTIVE
                profileComplete shouldBe false
            }

            assertSoftly(getMemberByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe mentorFixture.platform.provider
                name shouldBe mentorFixture.name
                nationality shouldBe Nationality.KOREA
                introduction shouldBe mentorFixture.introduction
                profileImageUrl shouldBe mentorFixture.profileImageUrl
                role shouldBe Role.MENTOR

                // Effected
                platform.socialId shouldBe null
                platform.email shouldBe null
                status shouldBe Member.Status.INACTIVE
                profileComplete shouldBe false
            }
        }
    }

    private fun getLanguage(id: Long): List<AvailableLanguage> =
        em.createQuery(
            """
                SELECT al
                FROM AvailableLanguage al
                WHERE al.member.id = :id
            """.trimIndent(),
            AvailableLanguage::class.java,
        ).setParameter("id", id)
            .resultList

    private fun getSchedule(id: Long): List<Schedule> =
        em.createQuery(
            """
                SELECT s
                FROM Schedule s
                WHERE s.mentor.id = :id
            """.trimIndent(),
            Schedule::class.java,
        ).setParameter("id", id)
            .resultList

    private fun getMentorByJpql(id: Long): Mentor = mentorRepository.findByIdOrNull(id)!!

    private fun getMemberByJpql(id: Long): Member<*> = memberRepository.findByIdOrNull(id)!!

    private fun getMentorByNative(id: Long): Mentor =
        em.createNativeQuery(
            """
                SELECT *
                FROM mentor m1
                INNER JOIN member m2 ON m1.id = m2.id
                WHERE m1.id = :id
            """.trimIndent(),
            Mentor::class.java,
        ).setParameter("id", id)
            .singleResult as Mentor

    private fun getMemberByNative(id: Long): Member<*> =
        em.createNativeQuery(
            """
                SELECT *
                FROM member m
                LEFT JOIN mentor tor ON m.id = tor.id
                LEFT JOIN mentee tee ON m.id = tee.id
                WHERE m.id = :id
            """.trimIndent(),
            Member::class.java,
        ).setParameter("id", id)
            .singleResult as Member<*>
}
