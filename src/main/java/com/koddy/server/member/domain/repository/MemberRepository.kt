package com.koddy.server.member.domain.repository

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MemberRepository : JpaRepository<Member<*>, Long> {
    // @Query
    @KoddyWritableTransactional
    @Modifying
    @Query(
        value = """
                UPDATE member
                SET status = 'INACTIVE',
                    social_id = null,
                    email = null,
                    profile_complete = false
                WHERE id = :id
                """,
        nativeQuery = true,
    )
    fun deleteMember(@Param("id") id: Long)

    // Query Method
    override fun existsById(id: Long): Boolean

    fun existsByPlatformSocialId(socialId: String): Boolean

    fun findByPlatformSocialId(socialId: String): Member<*>?

    fun findByPlatformEmail(email: Email): Member<*>?
}
