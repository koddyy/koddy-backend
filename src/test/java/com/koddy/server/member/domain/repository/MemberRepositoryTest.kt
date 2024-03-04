package com.koddy.server.member.domain.repository

import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_2
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_2
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

@RepositoryTestKt
@DisplayName("Member -> MemberRepository 테스트")
internal class MemberRepositoryTest(
    private val sut: MemberRepository,
    private val em: EntityManager,
) {
    @Test
    fun `소셜 플랫폼 고유 ID를 기반으로 사용자를 조회한다`() {
        // given
        val mentor: Mentor = sut.save(MENTOR_1.toDomain())
        val mentee: Mentee = sut.save(MENTEE_1.toDomain())

        // when - then
        assertSoftly {
            sut.findByPlatformSocialId(MENTOR_1.platform.socialId!!) shouldBe mentor
            sut.findByPlatformSocialId("${MENTOR_1.platform.socialId!!}7") shouldBe null
            sut.findByPlatformSocialId(MENTEE_1.platform.socialId!!) shouldBe mentee
            sut.findByPlatformSocialId("${MENTEE_1.platform.socialId!!}7") shouldBe null
        }
    }

    @Test
    fun `특정 소셜 플랫폼 고유 ID로 가입된 사용자가 존재하는지 확인한다`() {
        // given
        sut.save(MENTOR_1.toDomain())
        sut.save(MENTEE_1.toDomain())

        // when - then
        assertSoftly {
            sut.existsByPlatformSocialId(MENTOR_1.platform.socialId!!) shouldBe true
            sut.existsByPlatformSocialId(MENTOR_2.platform.socialId!!) shouldBe false
            sut.existsByPlatformSocialId(MENTEE_1.platform.socialId!!) shouldBe true
            sut.existsByPlatformSocialId(MENTEE_2.platform.socialId!!) shouldBe false
        }
    }

    @Test
    fun `사용자를 삭제한다 (Soft Delete)`() {
        // given
        val member: Member<*> = sut.save(MENTOR_1.toDomain())

        // when
        sut.deleteMember(member.id)

        // then
        em.flush()
        em.clear()

        assertSoftly {
            getMemberByJpql(member.id) shouldBe null
            getMemberByNative(member.id) shouldNotBe null

            val findMember: Member<*> = getMemberByNative(member.id)
            assertSoftly(findMember) {
                platform.provider shouldBe MENTOR_1.platform.provider
                platform.socialId shouldBe null
                platform.email shouldBe null
                name shouldBe MENTOR_1.getName()
                profileImageUrl shouldBe MENTOR_1.profileImageUrl
                nationality shouldBe Nationality.KOREA
                introduction shouldBe MENTOR_1.introduction
                isProfileComplete shouldBe false
                role shouldBe Role.MENTOR
                status shouldBe Member.Status.INACTIVE
            }
        }
    }

    private fun getMemberByJpql(id: Long): Member<*>? = sut.findByIdOrNull(id)

    private fun getMemberByNative(id: Long): Member<*> {
        return em.createNativeQuery(
            """
            SELECT *
            FROM member
            LEFT JOIN mentor m1 on member.id = m1.id
            LEFT JOIN mentee m2 on member.id = m2.id
            WHERE member.id = :id
            """,
            Member::class.java,
        ).setParameter("id", id)
            .singleResult as Member<*>
    }
}
