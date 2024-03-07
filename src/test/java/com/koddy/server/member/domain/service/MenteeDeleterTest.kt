package com.koddy.server.member.domain.service

import com.koddy.server.common.IntegrateTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.member.domain.model.AvailableLanguage
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.MenteeRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
    companion object {
        private val menteeFixture = menteeFixture(sequence = 1)
    }

    @Test
    fun `멘티를 삭제한다 (Soft Delete)`() {
        // given
        val mentee: Member<*> = memberRepository.save(menteeFixture.toDomain())
        assertSoftly {
            getLanguage(mentee.id) shouldHaveSize menteeFixture.languages.size
            getMenteeByJpql(mentee.id) shouldNotBe null
            getMemberByJpql(mentee.id) shouldNotBe null
            getMenteeByNative(mentee.id) shouldNotBe null
            getMemberByNative(mentee.id) shouldNotBe null

            assertSoftly(getMenteeByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe menteeFixture.platform.provider
                name shouldBe menteeFixture.name
                nationality shouldBe menteeFixture.nationality
                introduction shouldBe menteeFixture.introduction
                profileImageUrl shouldBe menteeFixture.profileImageUrl
                role shouldBe Role.MENTEE
                interest.school shouldBe menteeFixture.interest.school
                interest.major shouldBe menteeFixture.interest.major

                // Effected
                platform.socialId shouldBe menteeFixture.platform.socialId
                platform.email?.value shouldBe menteeFixture.platform.email?.value
                status shouldBe Member.Status.ACTIVE
                isProfileComplete shouldBe true
            }

            assertSoftly(getMemberByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe menteeFixture.platform.provider
                name shouldBe menteeFixture.name
                nationality shouldBe menteeFixture.nationality
                introduction shouldBe menteeFixture.introduction
                profileImageUrl shouldBe menteeFixture.profileImageUrl
                role shouldBe Role.MENTEE

                // Effected
                platform.socialId shouldBe menteeFixture.platform.socialId
                platform.email?.value shouldBe menteeFixture.platform.email?.value
                status shouldBe Member.Status.ACTIVE
                isProfileComplete shouldBe true
            }
        }

        // when
        sut.execute(mentee.id)

        // then
        assertSoftly {
            getLanguage(mentee.id) shouldHaveSize menteeFixture.languages.size
            getMenteeByJpql(mentee.id) shouldBe null
            getMemberByJpql(mentee.id) shouldBe null
            getMenteeByNative(mentee.id) shouldNotBe null
            getMemberByNative(mentee.id) shouldNotBe null

            assertSoftly(getMenteeByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe menteeFixture.platform.provider
                name shouldBe menteeFixture.name
                nationality shouldBe menteeFixture.nationality
                introduction shouldBe menteeFixture.introduction
                profileImageUrl shouldBe menteeFixture.profileImageUrl
                role shouldBe Role.MENTEE
                interest.school shouldBe menteeFixture.interest.school
                interest.major shouldBe menteeFixture.interest.major

                // Effected
                platform.socialId shouldBe null
                platform.email shouldBe null
                status shouldBe Member.Status.INACTIVE
                isProfileComplete shouldBe false
            }

            assertSoftly(getMemberByNative(mentee.id)) {
                // Not Effected
                platform.provider shouldBe menteeFixture.platform.provider
                name shouldBe menteeFixture.name
                nationality shouldBe menteeFixture.nationality
                introduction shouldBe menteeFixture.introduction
                profileImageUrl shouldBe menteeFixture.profileImageUrl
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

    private fun getMenteeByJpql(id: Long): Mentee? = menteeRepository.findByIdOrNull(id)

    private fun getMemberByJpql(id: Long): Member<*>? = memberRepository.findByIdOrNull(id)

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
