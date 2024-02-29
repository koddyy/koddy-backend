package com.koddy.server.member.domain.service

import com.koddy.server.common.IntegrateTestKt
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
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

@IntegrateTestKt
@io.kotest.core.annotation.DisplayName("Member -> MentorDeleter 테스트 [IntegrateTest]")
internal class MentorDeleterTest(
    private val sut: MentorDeleter,
    private val memberRepository: MemberRepository,
    private val mentorRepository: MentorRepository,
    private val em: EntityManager,
) {
    @Test
    fun `멘토를 삭제한다 (Soft Delete)`() {
        // given
        val mentor: Member<*> = memberRepository.save(MENTOR_1.toDomain())
        assertSoftly {
            getLanguage(mentor.id) shouldHaveSize MENTOR_1.languages.size
            getSchedule(mentor.id) shouldHaveSize MENTOR_1.timelines.size
            getMentorByJpql(mentor.id) shouldNotBe null
            getMemberByJpql(mentor.id) shouldNotBe null
            getMentorByNative(mentor.id) shouldNotBe null
            getMemberByNative(mentor.id) shouldNotBe null

            assertSoftly(getMentorByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe MENTOR_1.platform.provider
                name shouldBe MENTOR_1.getName()
                nationality shouldBe Nationality.KOREA
                introduction shouldBe MENTOR_1.introduction
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
                role shouldBe Role.MENTOR
                universityProfile.school shouldBe MENTOR_1.universityProfile.school
                universityProfile.major shouldBe MENTOR_1.universityProfile.major
                universityProfile.enteredIn shouldBe MENTOR_1.universityProfile.enteredIn
                universityAuthentication shouldBe null
                mentoringPeriod.startDate shouldBe MENTOR_1.mentoringPeriod.startDate
                mentoringPeriod.endDate shouldBe MENTOR_1.mentoringPeriod.endDate
                mentoringPeriod.timeUnit shouldBe MENTOR_1.mentoringPeriod.timeUnit

                // Effected
                platform.socialId shouldBe MENTOR_1.platform.socialId
                platform.email.value shouldBe MENTOR_1.platform.email.value
                status shouldBe Member.Status.ACTIVE
                isProfileComplete shouldBe true
            }

            assertSoftly(getMemberByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe MENTOR_1.platform.provider
                name shouldBe MENTOR_1.getName()
                nationality shouldBe Nationality.KOREA
                introduction shouldBe MENTOR_1.introduction
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
                role shouldBe Role.MENTOR

                // Effected
                platform.socialId shouldBe MENTOR_1.platform.socialId
                platform.email.value shouldBe MENTOR_1.platform.email.value
                status shouldBe Member.Status.ACTIVE
                isProfileComplete shouldBe true
            }
        }

        // when
        sut.execute(mentor.id)

        // then
        assertSoftly {
            getLanguage(mentor.id) shouldHaveSize MENTOR_1.languages.size
            getSchedule(mentor.id) shouldHaveSize 0
            shouldThrow<MemberException> {
                getMentorByJpql(mentor.id)
                getMemberByJpql(mentor.id)
            } shouldHaveMessage MENTOR_NOT_FOUND.message
            getMentorByNative(mentor.id) shouldNotBe null
            getMemberByNative(mentor.id) shouldNotBe null

            assertSoftly(getMentorByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe MENTOR_1.platform.provider
                name shouldBe MENTOR_1.getName()
                nationality shouldBe Nationality.KOREA
                introduction shouldBe MENTOR_1.introduction
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
                role shouldBe Role.MENTOR
                universityProfile.school shouldBe MENTOR_1.universityProfile.school
                universityProfile.major shouldBe MENTOR_1.universityProfile.major
                universityProfile.enteredIn shouldBe MENTOR_1.universityProfile.enteredIn
                universityAuthentication shouldBe null
                mentoringPeriod.startDate shouldBe MENTOR_1.mentoringPeriod.startDate
                mentoringPeriod.endDate shouldBe MENTOR_1.mentoringPeriod.endDate
                mentoringPeriod.timeUnit shouldBe MENTOR_1.mentoringPeriod.timeUnit

                // Effected
                platform.socialId shouldBe null
                platform.email shouldBe null
                status shouldBe Member.Status.INACTIVE
                isProfileComplete shouldBe false
            }

            assertSoftly(getMemberByNative(mentor.id)) {
                // Not Effected
                platform.provider shouldBe MENTOR_1.platform.provider
                name shouldBe MENTOR_1.getName()
                nationality shouldBe Nationality.KOREA
                introduction shouldBe MENTOR_1.introduction
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
                role shouldBe Role.MENTOR

                // Effected
                platform.socialId shouldBe null
                platform.email shouldBe null
                status shouldBe Member.Status.INACTIVE
                isProfileComplete shouldBe false
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

    private fun getMentorByJpql(id: Long): Mentor = mentorRepository.getById(id)

    private fun getMemberByJpql(id: Long): Member<*> = memberRepository.getById(id)

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
