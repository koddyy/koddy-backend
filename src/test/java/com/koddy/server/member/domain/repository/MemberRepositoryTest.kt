package com.koddy.server.member.domain.repository

import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
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
    companion object {
        private val mentorFixtureA = mentorFixture(sequence = 1)
        private val mentorFixtureB = mentorFixture(sequence = 2)
        private val menteeFixtureA = menteeFixture(sequence = 1)
        private val menteeFixtureB = menteeFixture(sequence = 2)
    }

    @Test
    fun `소셜 플랫폼 고유 ID를 기반으로 사용자를 조회한다`() {
        // given
        val mentor: Mentor = sut.save(mentorFixtureA.toDomain())
        val mentee: Mentee = sut.save(menteeFixtureA.toDomain())

        // when - then
        assertSoftly {
            sut.findByPlatformSocialId(socialId = mentorFixtureA.platform.socialId!!) shouldBe mentor
            sut.findByPlatformSocialId(socialId = "${mentorFixtureA.platform.socialId!!}7") shouldBe null
            sut.findByPlatformSocialId(socialId = menteeFixtureA.platform.socialId!!) shouldBe mentee
            sut.findByPlatformSocialId(socialId = "${menteeFixtureA.platform.socialId!!}7") shouldBe null
        }
    }

    @Test
    fun `특정 소셜 플랫폼 고유 ID로 가입된 사용자가 존재하는지 확인한다`() {
        // given
        sut.save(mentorFixtureA.toDomain())
        sut.save(menteeFixtureA.toDomain())

        // when - then
        assertSoftly {
            sut.existsByPlatformSocialId(mentorFixtureA.platform.socialId!!) shouldBe true
            sut.existsByPlatformSocialId(mentorFixtureB.platform.socialId!!) shouldBe false
            sut.existsByPlatformSocialId(menteeFixtureA.platform.socialId!!) shouldBe true
            sut.existsByPlatformSocialId(menteeFixtureB.platform.socialId!!) shouldBe false
        }
    }

    @Test
    fun `사용자를 삭제한다 (Soft Delete)`() {
        // given
        val member: Member<*> = sut.save(mentorFixtureA.toDomain())

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
                platform.provider shouldBe mentorFixtureA.platform.provider
                platform.socialId shouldBe null
                platform.email shouldBe null
                name shouldBe mentorFixtureA.name
                profileImageUrl shouldBe mentorFixtureA.profileImageUrl
                nationality shouldBe Nationality.KOREA
                introduction shouldBe mentorFixtureA.introduction
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
