package com.koddy.server.member.domain.service

import com.koddy.server.common.IntegrateTestKt
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.member.domain.model.AvailableLanguage
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.MENTEE_NOT_FOUND
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

@IntegrateTestKt
@DisplayName("Member -> MenteeDeleter 테스트 [IntegrateTest]")
internal class MenteeDeleterTest(
    private val sut: MenteeDeleter,
    private val memberRepository: MemberRepository,
    private val menteeRepository: MenteeRepository,
    private val em: EntityManager,
) {
    @Test
    fun `멘티를 삭제한다 (Soft Delete)`() {
        // given
        val mentee: Member<*> = memberRepository.save(MENTEE_1.toDomain())
        assertSoftly {
            getLanguage(mentee.id) shouldHaveSize MENTEE_1.languages.size
            getMenteeByJpql(mentee.id) shouldNotBe null
            getMemberByJpql(mentee.id) shouldNotBe null
            getMenteeByNative(mentee.id) shouldNotBe null
            getMemberByNative(mentee.id) shouldNotBe null

            assertSoftly(getMenteeByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe MENTEE_1.platform.provider
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality
                introduction shouldBe MENTEE_1.introduction
                profileImageUrl shouldBe MENTEE_1.profileImageUrl
                role shouldBe Role.MENTEE
                interest.school shouldBe MENTEE_1.interest.school
                interest.major shouldBe MENTEE_1.interest.major

                // Effected
                platform.socialId shouldBe MENTEE_1.platform.socialId
                platform.email.value shouldBe MENTEE_1.platform.email.value
                status shouldBe Member.Status.ACTIVE
                isProfileComplete shouldBe true
            }

            assertSoftly(getMemberByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe MENTEE_1.platform.provider
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality
                introduction shouldBe MENTEE_1.introduction
                profileImageUrl shouldBe MENTEE_1.profileImageUrl
                role shouldBe Role.MENTEE

                // Effected
                platform.socialId shouldBe MENTEE_1.platform.socialId
                platform.email.value shouldBe MENTEE_1.platform.email.value
                status shouldBe Member.Status.ACTIVE
                isProfileComplete shouldBe true
            }
        }

        // when
        sut.execute(mentee.id)

        // then
        assertSoftly {
            getLanguage(mentee.id) shouldHaveSize MENTEE_1.languages.size
            shouldThrow<MemberException> {
                getMenteeByJpql(mentee.id)
                getMemberByJpql(mentee.id)
            } shouldHaveMessage MENTEE_NOT_FOUND.message
            getMenteeByNative(mentee.id) shouldNotBe null
            getMemberByNative(mentee.id) shouldNotBe null

            assertSoftly(getMenteeByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe MENTEE_1.platform.provider
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality
                introduction shouldBe MENTEE_1.introduction
                profileImageUrl shouldBe MENTEE_1.profileImageUrl
                role shouldBe Role.MENTEE
                interest.school shouldBe MENTEE_1.interest.school
                interest.major shouldBe MENTEE_1.interest.major

                // Effected
                platform.socialId shouldBe null
                platform.email shouldBe null
                status shouldBe Member.Status.INACTIVE
                isProfileComplete shouldBe false
            }

            assertSoftly(getMemberByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe MENTEE_1.platform.provider
                name shouldBe MENTEE_1.getName()
                nationality shouldBe MENTEE_1.nationality
                introduction shouldBe MENTEE_1.introduction
                profileImageUrl shouldBe MENTEE_1.profileImageUrl
                role shouldBe Role.MENTEE

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

    private fun getMenteeByJpql(id: Long): Mentee = menteeRepository.findByIdOrNull(id)!!

    private fun getMemberByJpql(id: Long): Member<*> = memberRepository.findByIdOrNull(id)!!

    private fun getMenteeByNative(id: Long): Mentee =
        em.createNativeQuery(
            """
                SELECT *
                FROM mentee m1
                INNER JOIN member m2 ON m1.id = m2.id
                WHERE m1.id = :id
            """.trimIndent(),
            Mentee::class.java,
        ).setParameter("id", id)
            .singleResult as Mentee

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
