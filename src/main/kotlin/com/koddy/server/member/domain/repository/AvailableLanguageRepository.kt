package com.koddy.server.member.domain.repository

import com.koddy.server.member.domain.model.AvailableLanguage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AvailableLanguageRepository : JpaRepository<AvailableLanguage, Long> {
    @Query(
        value = """
                SELECT *
                FROM member_language
                WHERE member_id = :memberId
                """,
        nativeQuery = true,
    )
    fun findByMemberIdWithNative(@Param("memberId") memberId: Long): List<AvailableLanguage>
}
